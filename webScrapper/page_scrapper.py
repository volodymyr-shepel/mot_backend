import requests
from bs4 import BeautifulSoup
import re
import json
import sys


HEADERS = {'User-Agent': 'Mozilla/5.0 (iPad; CPU OS 12_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148'}

with open('./data/categories_info.json', 'r', encoding="utf-8") as file:
    data = json.load(file)

# Extract CategoryUrl values
category_urls = [item['CategoryUrl'] for item in data]


# Move the initialization of output_data outside the loop
output_data = {}


for url in category_urls:
    # Send a GET request to the URL
    response = requests.get(url, headers=HEADERS)

    # Check if the request was successful (status code 200)
    if response.status_code == 200:
        # Parse the HTML content using BeautifulSoup
        soup = BeautifulSoup(response.text, 'html.parser')

        a_elements = soup.find_all('a', class_='sc-1h16fat-0 sc-1yu46qn-7 bCpqs')

        # Extract href values into a list
        product_list = ['https://www.x-kom.pl' + a_element.get('href') for a_element in a_elements]

        category_number = re.search(r'/c/(\d+)', url).group(1)

        # Move this line inside the loop to append data for each category
        output_data[category_number] = []

        for product_url in product_list[:15]:
            product_data = {}
            print(product_url)

            try:
                r = requests.get(product_url, headers=HEADERS)
                product_soup = BeautifulSoup(r.text, 'html.parser')

                # Extract product description
                # if we want to scrap all descriptions remove last find(now it will scrap only first section)
                product_description_section = product_soup.find('section', class_='product-description content product-page').find('div', class_="row text-left fresh-content")
                product_description = ' '.join([p.text.strip() for p in product_description_section.find_all('p')]) if product_description_section else ""
                # Joining text from all <p> tags within the product description
                product_price = product_soup.find("div",class_="sc-n4n86h-1 hYfBFq").text
                # Extract numerical value without currency
                product_price = re.sub(r'[^0-9,]', '', product_price)

                # Replace commas with dots and convert to float
                product_price = float(product_price.replace(',', '.'))


                product_title = product_soup.find('h1', {'data-name': 'productTitle'}).text.strip()

                element_with_image = product_soup.find('div', class_='sc-jiiyfe-1 ffWNLr')
                img_element = element_with_image.find('img')
                image_url = img_element.get('src')

                
                specification_section = product_soup.find('div', class_="sc-13p5mv-2 fxqQxb")

                if specification_section:
                    specification_fields = specification_section.find_all('div', class_="sc-1s1zksu-0 sc-1s1zksu-1 hHQkLn sc-13p5mv-0 VGBov")

                    specification_data = {}

                    for specification_field in specification_fields:
                        key = specification_field.find('div', class_="sc-1s1zksu-0 kmPqDP").text.strip()
                        value = specification_field.find('div', class_="sc-13p5mv-3 UfEQd").text.strip()


                        specification_data[key] = value

                    product_data["ImageUrl"] = image_url
                    product_data["Description"] = product_description
                    product_data["Price"] = product_price
                    product_data["Specification"] = specification_data
                    

                    output_data[category_number].append({product_title: product_data})
            

            except Exception as e:
                print(f"An exception occurred for {product_url}: {e}")
                continue  # Continue to the next product if an exception occurs

    else:
        print(f"Failed to fetch content. Status code: {response.status_code}")
        sys.exit()
    

print("WRITING TO THE FILE")
# Save the content to a JSON file (outside the loop)
with open("./data/output.json", "w", encoding="utf-8") as output_file:
    json.dump(output_data, output_file, indent=2)

print("Content saved to output.json")



# TODO : SO NOW IT WORKS STILL NEED SMALL MODIFICATIONS
