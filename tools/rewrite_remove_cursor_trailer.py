import os
import subprocess


TRAILER_LINE = "Made-with: Cursor"


def sh(*args: str) -> str:
    return subprocess.check_output(list(args)).decode().strip()


def parse_ident(line: str):
    # line format: "Name <email> unix_timestamp tz"
    name_email, ts, tz = line.rsplit(" ", 2)
    name, email = name_email.rsplit(" <", 1)
    email = email[:-1]
    return name, email, ts, tz


def main():
    # Walk commits in topological order from root to HEAD, recreating each commit
    # with identical tree/parents/idents, but with the Cursor trailer stripped.
    rev_list = (
        subprocess.check_output(
            ["git", "rev-list", "--reverse", "--topo-order", "--parents", "HEAD"]
        )
        .decode()
        .splitlines()
    )

    new_of: dict[str, str] = {}
    for line in rev_list:
        parts = line.split()
        old = parts[0]
        parents = parts[1:]

        raw = subprocess.check_output(["git", "cat-file", "-p", old]).decode(
            "utf-8", errors="replace"
        )
        header, msg = raw.split("\n\n", 1)

        author = None
        committer = None
        for h in header.splitlines():
            if h.startswith("author "):
                author = h[len("author ") :]
            elif h.startswith("committer "):
                committer = h[len("committer ") :]

        if author is None or committer is None:
            raise RuntimeError(f"Missing author/committer for {old}")

        # Strip exact trailer line (idempotent)
        out_lines = []
        for l in msg.splitlines():
            if l.strip() == TRAILER_LINE:
                continue
            out_lines.append(l)
        new_msg = ("\n".join(out_lines)).rstrip() + "\n"

        tree = sh("git", "show", "-s", "--format=%T", old)
        new_parents = [new_of[p] for p in parents]

        an, ae, ats, atz = parse_ident(author)
        cn, ce, cts, ctz = parse_ident(committer)

        env = os.environ.copy()
        env.update(
            {
                "GIT_AUTHOR_NAME": an,
                "GIT_AUTHOR_EMAIL": ae,
                "GIT_AUTHOR_DATE": f"{ats} {atz}",
                "GIT_COMMITTER_NAME": cn,
                "GIT_COMMITTER_EMAIL": ce,
                "GIT_COMMITTER_DATE": f"{cts} {ctz}",
            }
        )

        cmd = ["git", "commit-tree", tree]
        for p in new_parents:
            cmd += ["-p", p]
        cmd += ["-F", "-"]

        new_commit = (
            subprocess.check_output(cmd, input=new_msg.encode("utf-8"), env=env)
            .decode()
            .strip()
        )
        new_of[old] = new_commit

    new_head = new_of[sh("git", "rev-parse", "HEAD")]
    subprocess.check_call(["git", "update-ref", "refs/heads/main", new_head])
    print(new_head)


if __name__ == "__main__":
    main()

