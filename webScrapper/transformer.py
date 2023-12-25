import json

def txt_to_json(file_path, delimiter=':'):
    data = {}

    with open(file_path, 'r') as file:
        for line in file:
            # Split the line only if the delimiter is present
            if delimiter in line:
                key, value = line.strip().split(delimiter, 1)
                data[key] = value

    return data

def save_json(data, json_path):
    with open(json_path, 'w') as json_file:
        json.dump(data, json_file, indent=2)

# Replace 'your_input.txt' and 'output.json' with your file paths
input_file_path = 'result.txt'
output_json_path = 'output.json'

data = txt_to_json(input_file_path)
save_json(data, output_json_path)

print(f'Conversion complete. JSON saved to {output_json_path}')
