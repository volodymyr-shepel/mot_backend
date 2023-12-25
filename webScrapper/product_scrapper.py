from bs4 import BeautifulSoup
import requests
import json


with open('./data/categories_info.json', 'r',encoding="utf-8") as file:
    data = json.load(file)

HEADERS = {'User-Agent': 'Mozilla/5.0 (iPad; CPU OS 12_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148'}


# Extract CategoryUrl values
category_urls = [item['CategoryUrl'] for item in data]

url = category_urls[0]

response = requests.get(url,headers=HEADERS)

if response.status_code == 200:
    # Parse the HTML content using BeautifulSoup
    soup = BeautifulSoup(response.text, 'html.parser')

    # Find the content you want to save
    content_to_save = soup.prettify()  # This includes the entire HTML structure

    # Save the content to a file
    with open("output.html", "w", encoding="utf-8") as output_file:
        output_file.write(content_to_save)

    
    print("Content saved to output.html")
else:
    print(f"Failed to fetch content. Status code: {response.status_code}")