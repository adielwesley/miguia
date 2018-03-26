#if 1
  #include <SPI.h>
  #include <PN532_SPI.h>
  #include "PN532.h"

  PN532_SPI pn532spi(SPI, 10);
  PN532 nfc(pn532spi);
#elif 0
  #include <PN532_HSU.h>
  #include <PN532.h>
      
  PN532_HSU pn532hsu(Serial1);
  PN532 nfc(pn532hsu);
#else 
  #include <Wire.h>
  #include <PN532_I2C.h>
  #include <PN532.h>
  #include <NfcAdapter.h>
  
  PN532_I2C pn532i2c(Wire);
  PN532 nfc(pn532i2c);
#endif
  
void setup(void) {
  Serial.begin(9600);
  nfc.begin();
  SPI.begin();
  Serial.println("Aproxime o seu cartao do leitor...");
  Serial.println();
  uint32_t versiondata = nfc.getFirmwareVersion();

  mensageminicial();
  nfc.SAMConfig();
}

void loop(void) {
  boolean success;
  uint8_t uid[] = { 0, 0, 0, 0, 0, 0, 0 };  // Buffer to store the returned UID
  uint8_t uidLength;                        // Length of the UID (4 or 7 bytes depending on ISO14443A card type)
  
  success = nfc.readPassiveTargetID(PN532_MIFARE_ISO14443A, &uid[0], &uidLength);
  
  if (success) {
    Serial.print("#");
    for (uint8_t i=0; i < uidLength; i++) 
    {
     Serial.print(uid[i] < 0x10 ? "0" : "");Serial.print(uid[i], HEX); 
    }
    Serial.println("~");
  }
}

void mensageminicial()
{
  //Serial.clear();
  Serial.print(" Aproxime o seu");  
  Serial.println("cartao do leitor");  
}

