import json

def transformFile():
    # Read the content from categories.txt
    with open("categories.txt", "r", encoding="utf-8") as txt_file:
        # Load the content as JSON
        data = json.loads(txt_file.read())

    # Write the JSON data to a new file (categories.json)
    with open("categories.json", "w", encoding="utf-8") as json_file:
        # Dump the data to JSON format
        json.dump(data, json_file, indent=2)  # You can adjust the 'indent' parameter for formatting

    print("Transformation completed. Check categories.json.")

def get_parent_categories():

    # Read the content from categories.json
    with open("categories.json", "r", encoding="utf-8") as json_file:
        # Load the content as JSON
        data = json.load(json_file)

    # Extract unique "Name" values from each "ParentGroup" without duplicates
    unique_names = list({entry["ParentGroup"]["Name"] for entry in data})

    # Write the unique names to a new file (unique_names.txt)
    with open("unique_parent_category_names.txt", "w", encoding="utf-8") as names_file:
        # Write each unique name on a new line
        names_file.write('\n'.join(unique_names))

    print("Extraction completed. Check unique_parent_category_names.txt.")

def extract_all_child_categories():
        # Read the content from categories.json
    with open("categories.json", "r", encoding="utf-8") as json_file:
        # Load the content as JSON
        data = json.load(json_file)

    # Extract "NamePlural" values
    names_plural = [entry["NamePlural"] for entry in data]


    # Write the unique NamePlural values to a new file (unique_name_plural.txt)
    with open("unique_name_plural.txt", "w", encoding="utf-8") as name_plural_file:
        # Write each unique NamePlural on a new line
        name_plural_file.write('\n'.join(names_plural))

    print("Extraction completed. Check unique_name_plural.txt.")




def get_parent_and_subcategories():
    # Read the content from categories.json
    with open("./data/categories.json", "r", encoding="utf-8") as json_file:
        # Load the content as JSON
        data = json.load(json_file)

    # Organize categories into a hierarchical structure
    categories_hierarchy = {}

    for entry in data:
        parent_group_name = entry["ParentGroup"]["Name"]
        category_id = int(entry["Id"])
        category_name_plural = entry["NamePlural"]

        if parent_group_name not in categories_hierarchy:
            categories_hierarchy[parent_group_name] = {
                "CategoryId": int(entry["ParentGroup"]["Id"]),
                "subcategories": []
            }

        subcategory = {
            "CategoryId": category_id,
            "NamePlural": category_name_plural
        }

        categories_hierarchy[parent_group_name]["subcategories"].append(subcategory)

    # Write the hierarchical structure to a new file (categories_hierarchy.json)
    with open("./data/categories_hierarchy.json", "w", encoding="utf-8") as hierarchy_file:
        json.dump(categories_hierarchy, hierarchy_file, indent=2)

    print("Extraction completed. Check categories_hierarchy.json.")

get_parent_and_subcategories()



#extract_all_child_categories()
#get_parent_and_subcategories()
# /g/2-laptopy-i-komputery.html
# /g/4-smartfony-i-smartwatche.html
# /g/7-gaming-i-streaming.html
# /g/5-podzespoly-komputerowe.html
# /g/6-urzadzenia-peryferyjne.html
# /g/8-tv-i-audio.html
# /g/64-smarthome-i-lifestyle.html
# /g/12-akcesoria.html