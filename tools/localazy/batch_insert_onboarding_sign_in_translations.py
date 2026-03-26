#!/usr/bin/env python3
"""
Batch insert or update screen_onboarding_signing_in and screen_onboarding_sign_in
in all login/impl values-*/translations.xml files.
Run from repo root. Does not modify localazy.xml.

Usage:
  python tools/localazy/batch_insert_onboarding_sign_in_translations.py
  python tools/localazy/batch_insert_onboarding_sign_in_translations.py --update
"""
from __future__ import annotations

import os
import re
import sys

LOGIN_RES = "features/login/impl/src/main/res"
ANCHOR_KEY = "screen_onboarding_sign_in_with_qr_code"
KEY_SIGNING_IN = "screen_onboarding_signing_in"
KEY_SIGN_IN = "screen_onboarding_sign_in"

TRANSLATIONS = {
    "values-be": ("Уваход...", "Увайсці"),
    "values-bg": ("Влизане...", "Влизане"),
    "values-cs": ("Přihlašování...", "Přihlásit se"),
    "values-cy": ("Yn mewngofnodi...", "Mewngofnodi"),
    "values-da": ("Logger ind...", "Log ind"),
    "values-de": ("Anmeldung läuft...", "Anmelden"),
    "values-el": ("Σύνδεση...", "Σύνδεση"),
    "values-en-rUS": ("Signing in...", "Sign in"),
    "values-es": ("Iniciando sesión...", "Iniciar sesión"),
    "values-et": ("Sisselogimine...", "Logi sisse"),
    "values-eu": ("Saioa hasten...", "Hasi saioa"),
    "values-fa": ("در حال ورود...", "ورود"),
    "values-fi": ("Kirjaudutaan...", "Kirjaudu sisään"),
    "values-fr": ("Connexion...", "Se connecter"),
    "values-hr": ("Prijava...", "Prijavi se"),
    "values-hu": ("Bejelentkezés...", "Bejelentkezés"),
    "values-in": ("Memproses masuk...", "Masuk"),
    "values-it": ("Accesso in corso...", "Accedi"),
    "values-ka": ("შესვლა...", "შესვლა"),
    "values-ko": ("로그인 중...", "로그인"),
    "values-lt": ("Prisijungiama...", "Prisijungti"),
    "values-nb": ("Logger inn...", "Logg inn"),
    "values-nl": ("Bezig met inloggen...", "Inloggen"),
    "values-pl": ("Logowanie...", "Zaloguj się"),
    "values-pt": ("A iniciar sessão...", "Iniciar sessão"),
    "values-pt-rBR": ("Entrando...", "Entrar"),
    "values-ro": ("Conectare...", "Conectați-vă"),
    "values-ru": ("Вход...", "Войти"),
    "values-sk": ("Prihlasovanie...", "Prihlásiť sa"),
    "values-sv": ("Loggar in...", "Logga in"),
    "values-tr": ("Oturum açılıyor...", "Giriş yap"),
    "values-uk": ("Вхід...", "Увійти"),
    "values-ur": ("داخل ہو رہے ہیں...", "داخل ہوں"),
    "values-uz": ("Tizimga kirish...", "Tizimga kiring"),
    "values-zh": ("正在登录...", "登录"),
    "values-zh-rTW": ("正在登入...", "登入"),
}


def escape_xml(value: str) -> str:
    return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")


def has_key(content: str, key: str) -> bool:
    return f'name="{key}"' in content


def insert_after_anchor(content: str, anchor: str, lines_to_insert: list[str]) -> str:
    if not lines_to_insert:
        return content
    if anchor in content:
        pattern = re.compile(r"^.*" + re.escape(anchor) + r".*$", re.MULTILINE)
        match = pattern.search(content)
        if match:
            insert_pos = match.end(0)
            before = content[:insert_pos]
            after = content[insert_pos:]
            return before + "\n" + "\n".join(lines_to_insert) + "\n" + after.lstrip("\n")
    pattern = re.compile(r"^\s*</resources>\s*$", re.MULTILINE)
    match = pattern.search(content)
    if match:
        insert_pos = match.start(0)
        before = content[:insert_pos].rstrip()
        after = content[insert_pos:]
        return before + "\n" + "\n".join(lines_to_insert) + "\n" + after
    return content


def replace_value(content: str, key: str, new_value: str) -> str:
    pattern = re.compile(r'(<string name="' + re.escape(key) + r'">)"(.*?)"(\s*</string>)', re.DOTALL)
    escaped = escape_xml(new_value)
    return pattern.sub(r'\1"' + escaped + r'"\3', content, count=1)


def process_file(path: str, signing_in: str, sign_in: str, update_mode: bool) -> bool:
    with open(path, "r", encoding="utf-8") as f:
        content = f.read()

    has_signing_in = has_key(content, KEY_SIGNING_IN)
    has_sign_in = has_key(content, KEY_SIGN_IN)

    new_content = content
    if update_mode:
        if has_signing_in:
            new_content = replace_value(new_content, KEY_SIGNING_IN, signing_in)
        if has_sign_in:
            new_content = replace_value(new_content, KEY_SIGN_IN, sign_in)
    else:
        lines_to_insert: list[str] = []
        if not has_signing_in:
            lines_to_insert.append(f'  <string name="{KEY_SIGNING_IN}">"{escape_xml(signing_in)}"</string>')
        if not has_sign_in:
            lines_to_insert.append(f'  <string name="{KEY_SIGN_IN}">"{escape_xml(sign_in)}"</string>')
        new_content = insert_after_anchor(new_content, ANCHOR_KEY, lines_to_insert)

    if new_content == content:
        return False

    with open(path, "w", encoding="utf-8", newline="\n") as f:
        f.write(new_content)
    return True


def main() -> int:
    update_mode = "--update" in sys.argv
    root = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
    res_dir = os.path.join(root, LOGIN_RES)
    if not os.path.isdir(res_dir):
        print(f"Error: not found: {res_dir}", file=sys.stderr)
        return 1

    modified = 0
    for values_folder, (signing_in, sign_in) in sorted(TRANSLATIONS.items()):
        path = os.path.join(res_dir, values_folder, "translations.xml")
        if not os.path.isfile(path):
            print(f"Skip (no file): {path}")
            continue
        if process_file(path, signing_in, sign_in, update_mode):
            action = "Updated" if update_mode else "Inserted"
            print(f"{action}: {path}")
            modified += 1

    print(f"Done. Modified {modified} file(s).")
    return 0


if __name__ == "__main__":
    sys.exit(main())
