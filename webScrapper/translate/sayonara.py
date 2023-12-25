from deep_translator import GoogleTranslator

def translate_and_write_chunk(chunk, source_lang='polish', target_lang='english'):
    translated_chunk = GoogleTranslator(source=source_lang, target=target_lang).translate(chunk)
    with open("result.txt", 'a', encoding='utf-8') as file:
        file.write(translated_chunk)

def main():
    chunk_size = 4000

    with open("./translate/data.txt", 'r', encoding='utf-8') as input_file:
        content = input_file.read()

        for i in range(0, len(content), chunk_size):
            chunk = content[i:i+chunk_size]
            translate_and_write_chunk(chunk)

if __name__ == "__main__":
    main()
