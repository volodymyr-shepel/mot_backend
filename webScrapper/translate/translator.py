from googletrans import Translator

def translate_file(input_file, output_file, source_language='pl', target_language='en'):
    # Read the content of the input file
    with open(input_file, 'r', encoding='utf-8') as file:
        text_to_translate = file.read()

    # Initialize the translator
    translator = Translator()

    # Translate the text
    translated_text = translator.translate(text_to_translate, src=source_language, dest=target_language).text

    # Write the translated text to the output file
    with open(output_file, 'w', encoding='utf-8') as file:
        file.write(translated_text)

if __name__ == "__main__":
    input_file_path = './translate/data.txt'  # Change this to the path of your input file
    output_file_path = 'output.txt'  # Change this to the desired path for the output file
    source_language = 'pl'  # Source language code (Polish)
    target_language = 'en'  # Target language code (English)

    translate_file(input_file_path, output_file_path, source_language, target_language)

    print(f'Translation complete. Translated content saved to {output_file_path}')
