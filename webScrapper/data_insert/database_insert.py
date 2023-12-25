import psycopg2
from datetime import datetime
import json

# Define your PostgreSQL connection parameters
db_params = {
    'host': 'localhost',  # Change to your PostgreSQL host
    'port': '5442',       # Change to your PostgreSQL port
    'user': 'admin',
    'password': 'root',
    'database': 'product'
}

# Connect to the PostgreSQL database
conn = psycopg2.connect(**db_params)
cursor = conn.cursor()

# Load JSON data from file
with open('./data/categories_hierarchy.json', 'r') as file:
    data = json.load(file)

# Define a function to recursively insert categories and subcategories
def insert_category(category_name, category, parent_category_id=None):
    category_id = category.get('CategoryId')  # Use the CategoryId from the JSON file
    name = category.get('NamePlural') or category.get('name', category_name)  # Use category_name for the top-level category
    created_on = updated_on = datetime.now()

    # Check if the category already exists with the same CategoryId
    cursor.execute("""
        SELECT id FROM category
        WHERE id = %s
    """, (category_id,))

    existing_category = cursor.fetchone()

    if existing_category:
        # Category with the same CategoryId already exists, update its information
        cursor.execute("""
            UPDATE category
            SET name = %s, parent_category_id = %s, created_on = %s, updated_on = %s
            WHERE id = %s
        """, (name, parent_category_id, created_on, updated_on, category_id))
    else:
        # Insert the category into the database
        cursor.execute("""
            INSERT INTO category (id, name, parent_category_id, created_on, updated_on)
            VALUES (%s, %s, %s, %s, %s)
        """, (category_id, name, parent_category_id, created_on, updated_on))

    # Recursively insert subcategories
    for subcategory in category.get('subcategories', []):
        insert_category(category_name, subcategory, parent_category_id=category_id)

# Insert top-level categories
for top_category_name, top_category_data in data.items():
    insert_category(top_category_name, top_category_data, parent_category_id=None)

# Commit the changes and close the connection
conn.commit()
cursor.close()
conn.close()
