#!/usr/bin/env python3
"""
Batch insert or update screen_preferences_section_customization and screen_preferences_section_security
in all preferences/impl values-*/translations.xml files.
Run from repo root. Does not modify localazy.xml.

Usage:
  python batch_insert_preferences_section_translations.py       # insert missing keys
  python batch_insert_preferences_section_translations.py --update   # update existing values from TRANSLATIONS
"""
from __future__ import annotations

import os
import re
import sys

# Repo-relative path to preferences translations
PREFERENCES_RES = "features/preferences/impl/src/main/res"
ANCHOR_KEY = "screen_notification_settings_title"
KEY_CUSTOMIZATION = "screen_preferences_section_customization"
KEY_SECURITY = "screen_preferences_section_security"

# (customization, security) per values-* folder name
# Refined for common settings UI; native speakers may further improve.
TRANSLATIONS = {
    "values-bg": ("Персонализация", "Сигурност"),
    "values-be": ("Персоналізацыя", "Бяспека"),
    "values-cs": ("Přizpůsobení", "Zabezpečení"),
    "values-cy": ("Cyfaddasu", "Diogelwch"),
    "values-da": ("Tilpasning", "Sikkerhed"),
    "values-de": ("Anpassung", "Sicherheit"),
    "values-el": ("Εξατομίκευση", "Ασφάλεια"),
    "values-en-rUS": ("Customization", "Security"),
    "values-es": ("Personalización", "Seguridad"),
    "values-et": ("Kohandamine", "Turvalisus"),
    "values-eu": ("Pertsonalizazioa", "Segurtasuna"),
    "values-fa": ("سفارشی‌سازی", "امنیت"),
    "values-fi": ("Mukautus", "Tietoturva"),
    "values-fr": ("Personnalisation", "Sécurité"),
    "values-hr": ("Prilagodba", "Sigurnost"),
    "values-hu": ("Testreszabás", "Biztonság"),
    "values-in": ("Kustomisasi", "Keamanan"),
    "values-it": ("Personalizzazione", "Sicurezza"),
    "values-ka": ("მორგება", "უსაფრთხოება"),
    "values-ko": ("맞춤 설정", "보안"),
    "values-lt": ("Tinklinimas", "Saugumas"),
    "values-nb": ("Tilpasning", "Sikkerhet"),
    "values-nl": ("Aanpassing", "Beveiliging"),
    "values-pl": ("Dostosowanie", "Bezpieczeństwo"),
    "values-pt": ("Personalização", "Segurança"),
    "values-pt-rBR": ("Personalização", "Segurança"),
    "values-ro": ("Personalizare", "Securitate"),
    "values-ru": ("Персонализация", "Безопасность"),
    "values-sk": ("Prispôsobenie", "Bezpečnosť"),
    "values-sv": ("Anpassning", "Säkerhet"),
    "values-tr": ("Özelleştirme", "Güvenlik"),
    "values-uk": ("Персоналізація", "Безпека"),
    "values-ur": ("حسب ضرورت", "سیکیورٹی"),
    "values-uz": ("Moslashtirish", "Xavfsizlik"),
    "values-zh": ("自定义", "安全"),
    "values-zh-rTW": ("自訂", "安全"),
}


def escape_xml(value: str) -> str:
    """Escape for XML string content. Keep quotes as-is for Android resource."""
    return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")


def insert_after_anchor(content: str, anchor: str, lines_to_insert: list[str]) -> str:
    """Insert lines after the first line that contains anchor. If anchor not found, insert before </resources>."""
    if anchor in content:
        pattern = re.compile(r"^.*" + re.escape(anchor) + r".*$", re.MULTILINE)
        match = pattern.search(content)
        if match:
            insert_pos = match.end(0)  # after the anchor line
            before = content[:insert_pos]
            after = content[insert_pos:]
            sep = "\n" if before.endswith("\n") else "\n"
            return before + sep + "\n".join(lines_to_insert) + "\n" + after.lstrip("\n")
    # Fallback: insert before </resources>
    pattern = re.compile(r"^\s*</resources>\s*$", re.MULTILINE)
    match = pattern.search(content)
    if match:
        insert_pos = match.start(0)
        before = content[:insert_pos].rstrip()
        after = content[insert_pos:]
        return before + "\n" + "\n".join(lines_to_insert) + "\n" + after
    return content


def process_file(path: str, customization: str, security: str) -> bool:
    """Return True if file was modified (insert mode)."""
    with open(path, "r", encoding="utf-8") as f:
        content = f.read()
    if KEY_CUSTOMIZATION in content and KEY_SECURITY in content:
        return False
    line_custom = '  <string name="' + KEY_CUSTOMIZATION + '">"' + escape_xml(customization) + '"</string>'
    line_security = '  <string name="' + KEY_SECURITY + '">"' + escape_xml(security) + '"</string>'
    new_content = insert_after_anchor(content, ANCHOR_KEY, [line_custom, line_security])
    if new_content == content:
        return False
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        f.write(new_content)
    return True


def replace_value(content: str, key: str, new_value: str) -> str:
    """Replace the string value for key in XML. Returns new content or unchanged."""
    pattern = re.compile(
        r'(<string name="' + re.escape(key) + r'">)"(.*?)"(\s*</string>)',
        re.DOTALL,
    )
    escaped = escape_xml(new_value)
    new_line = r'\1"' + escaped + r'"\3'
    return pattern.sub(new_line, content, count=1)


def update_file(path: str, customization: str, security: str) -> bool:
    """Replace existing key values (update mode). Return True if file was modified."""
    with open(path, "r", encoding="utf-8") as f:
        content = f.read()
    if KEY_CUSTOMIZATION not in content and KEY_SECURITY not in content:
        return False
    new_content = content
    if KEY_CUSTOMIZATION in content:
        new_content = replace_value(new_content, KEY_CUSTOMIZATION, customization)
    if KEY_SECURITY in content:
        new_content = replace_value(new_content, KEY_SECURITY, security)
    if new_content == content:
        return False
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        f.write(new_content)
    return True


def main() -> int:
    do_update = "--update" in sys.argv
    root = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
    res_dir = os.path.join(root, PREFERENCES_RES)
    if not os.path.isdir(res_dir):
        print(f"Error: not found: {res_dir}", file=sys.stderr)
        return 1
    modified = 0
    for values_folder, (customization, security) in sorted(TRANSLATIONS.items()):
        path = os.path.join(res_dir, values_folder, "translations.xml")
        if not os.path.isfile(path):
            print(f"Skip (no file): {path}")
            continue
        if do_update:
            if update_file(path, customization, security):
                print(f"Updated: {path}")
                modified += 1
        else:
            if process_file(path, customization, security):
                print(f"Inserted: {path}")
                modified += 1
    print(f"Done. Modified {modified} file(s).")
    return 0


if __name__ == "__main__":
    sys.exit(main())
