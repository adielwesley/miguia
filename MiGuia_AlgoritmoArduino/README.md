No projeto MiGuia utilizamos os seguintes componentes:
- arduino pro mini;
- m�dulo bluetooth HC-05
- m�dulo NFC PN532 para l�r as tags RFID. No m�dulo NFC usamos a comunica��o SPI.
- tags RFID.

O algoritmo faz o seguinte processamento:
- ler as Tags RFID com o m�dulo PN532
- transmitir a informa��o via bluetooth com o m�dulo HC-05 para o celular process�-la.
=========================================================================

COMO EXECUTAR O C�DIGO DO ARDUINO?

=========================================================================


O c�digo SD_FInal.ino deve ser transferido para a placa arduino usando a IDE Arduino (Dispon�vel em: <https://www.arduino.cc/en/Main/Software>).
