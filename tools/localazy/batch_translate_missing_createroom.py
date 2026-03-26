#!/usr/bin/env python3
from __future__ import annotations

import json
import re
import time
from pathlib import Path
from typing import Dict, Tuple

import requests

RES_DIR = Path("features/createroom/impl/src/main/res")
SOURCE_FILE = RES_DIR / "values/localazy.xml"

LOCALE_TO_LANG = {
    "values-be": "be",
    "values-bg": "bg",
    "values-cs": "cs",
    "values-cy": "cy",
    "values-da": "da",
    "values-de": "de",
    "values-el": "el",
    "values-es": "es",
    "values-et": "et",
    "values-eu": "eu",
    "values-fa": "fa",
    "values-fi": "fi",
    "values-fr": "fr",
    "values-hr": "hr",
    "values-hu": "hu",
    "values-in": "id",
    "values-it": "it",
    "values-ka": "ka",
    "values-ko": "ko",
    "values-lt": "lt",
    "values-nb": "no",
    "values-nl": "nl",
    "values-pl": "pl",
    "values-pt": "pt",
    "values-pt-rBR": "pt",
    "values-ro": "ro",
    "values-ru": "ru",
    "values-sk": "sk",
    "values-sv": "sv",
    "values-tr": "tr",
    "values-uk": "uk",
    "values-ur": "ur",
    "values-uz": "uz",
    "values-zh": "zh-CN",
    "values-zh-rTW": "zh-TW",
}

STRING_RE = re.compile(r'<string name="([^"]+)">"(.*?)"</string>', re.DOTALL)
KEY_RE = re.compile(r'<string name="([^"]+)">', re.DOTALL)
PLACEHOLDER_RE = re.compile(r"%\d+\$[sdf]")


def parse_source_strings() -> list[tuple[str, str]]:
    text = SOURCE_FILE.read_text(encoding="utf-8")
    return [(m.group(1), m.group(2)) for m in STRING_RE.finditer(text)]


def protect_text(text: str) -> tuple[str, Dict[str, str]]:
    token_map: Dict[str, str] = {}
    idx = 0

    def repl(match: re.Match[str]) -> str:
        nonlocal idx
        token = f"PH_TOKEN_{idx}"
        idx += 1
        token_map[token] = match.group(0)
        return token

    protected = PLACEHOLDER_RE.sub(repl, text)
    protected = protected.replace("\n", " NL_TOKEN ")
    return protected, token_map


def restore_text(text: str, token_map: Dict[str, str]) -> str:
    out = text
    for token, value in token_map.items():
        out = out.replace(token, value)
    out = out.replace(" NL_TOKEN ", "\n")
    return out


def escape_xml(value: str) -> str:
    return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")


def translate_text(text: str, target_lang: str, timeout: int = 15) -> str:
    url = "https://translate.googleapis.com/translate_a/single"
    params = {
        "client": "gtx",
        "sl": "en",
        "tl": target_lang,
        "dt": "t",
        "q": text,
    }
    response = requests.get(url, params=params, timeout=timeout)
    response.raise_for_status()
    data = response.json()
    translated_parts = [part[0] for part in data[0] if part and part[0] is not None]
    return "".join(translated_parts)


def main() -> int:
    src_items = parse_source_strings()
    src_keys = [k for k, _ in src_items]
    src_map = dict(src_items)

    cache: Dict[Tuple[str, str], str] = {}
    modified = 0

    for locale_dir, lang in LOCALE_TO_LANG.items():
        file_path = RES_DIR / locale_dir / "translations.xml"
        if not file_path.exists():
            continue

        content = file_path.read_text(encoding="utf-8")
        existing_keys = {m.group(1) for m in KEY_RE.finditer(content)}
        missing_keys = [k for k in src_keys if k not in existing_keys]
        if not missing_keys:
            continue

        lines = []
        for key in missing_keys:
            source_value = src_map[key]
            protected, token_map = protect_text(source_value)
            cache_key = (lang, protected)
            if cache_key in cache:
                translated = cache[cache_key]
            else:
                try:
                    translated = translate_text(protected, lang)
                    cache[cache_key] = translated
                    time.sleep(0.08)
                except Exception:
                    translated = protected
            final_value = restore_text(translated, token_map)
            lines.append(f'  <string name="{key}">"{escape_xml(final_value)}"</string>')

        if lines:
            insertion = "\n" + "\n".join(lines) + "\n"
            new_content = content.replace("</resources>", insertion + "</resources>")
            if new_content != content:
                file_path.write_text(new_content, encoding="utf-8", newline="\n")
                modified += 1
                print(f"Updated {locale_dir}: +{len(lines)} keys", flush=True)

    print(f"Done. Modified {modified} files.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
