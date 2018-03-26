No projeto MiGuia utilizamos os seguintes componentes:
- arduino pro mini;
- módulo bluetooth HC-05
- módulo NFC PN532 para lêr as tags RFID. No módulo NFC usamos a comunicação SPI.
- tags RFID.

O algoritmo faz o seguinte processamento:
- ler as Tags RFID com o módulo PN532
- transmitir a informação via bluetooth com o módulo HC-05 para o celular processá-la.
=========================================================================

COMO EXECUTAR O CÓDIGO DO ARDUINO?

=========================================================================


O código SD_FInal.ino deve ser transferido para a placa arduino usando a IDE Arduino (Disponível em: <https://www.arduino.cc/en/Main/Software>).
