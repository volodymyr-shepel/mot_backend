import json
import unicodedata
import re
def remove_polish_chars(text):
    # Replace Polish characters with their English counterparts
    polish_mapping = {
        'ą': 'a', 'ć': 'c', 'ę': 'e',
        'ł': 'l', 'ń': 'n', 'ó': 'o',
        'ś': 's', 'ż': 'z', 'ź': 'z'
    }

    # Replace Polish characters with their English counterparts
    text = ''.join(polish_mapping.get(char, char) for char in text)

    # Remove diacritic marks
    return ''.join(char for char in unicodedata.normalize('NFD', text) if unicodedata.category(char) != 'Mn')

def format_name_plural(name_plural):
    # Format the name plural from e.g., "Dyski SSD" to "dyski-ssd"
    return '-'.join(remove_polish_chars(name_plural.lower()).split()).replace('(','').replace(')','')

def extract_all_child_categories():
    # Read the content from categories.json
    with open("./data/categories.json", "r", encoding="utf-8") as json_file:
        # Load the content as JSON
        data = json.load(json_file)

    # Extract "Id," "NamePlural," "ParentGroup," and "Name" values
    categories_info = [
        {
            "Id": entry["Id"],
            "FormattedName": format_name_plural(entry["NamePlural"]),
            "ParentGroupId": entry.get("ParentGroup", {}).get("Id"),
            "NamePlural": entry.get("NamePlural"),  # Original Name
            "CategoryUrl": f"https://www.x-kom.pl/g-{entry.get('ParentGroup', {}).get('Id', '')}/c/{entry['Id']}-{format_name_plural(entry['NamePlural'])}.html"
        } for entry in data
    ]

    # Write the Id, formatted NamePlural, ParentGroupId, Name, and CategoryUrl values to a new JSON file (categories_info.json)
    with open("./data/categories_info.json", "w", encoding="utf-8") as json_output_file:
        # Write the data to the JSON file
        json.dump(categories_info, json_output_file, indent=4, ensure_ascii=False)

    print("Extraction completed. Check categories_info.json.")

# Call the function to extract and write the data
extract_all_child_categories()
