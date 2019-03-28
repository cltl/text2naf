# text2naf


Module that takes text and meta data as input and creates a NAF file with the raw text layer.

Usage:
See the script folder for examples how to run the module. The software expects text as input in UTF-8 format.


The following parameters are accepted:

--textfile  <path to a text file; if omitted a text input stream is expected>
--language  <iso language code for text language: "en", "nl", "es", "it", etc.>
--uri       <unique resrouce identifier that uniquely identifies a source text>
--date      <an ISO date string that represent the document creation time: "yyyy-MM-dd'T'HH:mm:ssZ">

The uri and language parameters are obligatory. If no text file is provided, a text input stream is expected.
If no text file and no input stream, the program aborts. If no date string is provided,
the current system time is used to generate a document creation time.

