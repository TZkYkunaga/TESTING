sử dụng lệnh sau trong terminal/cmd:

keytool -genkeypair -alias serverkey -keyalg RSA -keysize 2048 -keystore serverkeystore.jks -validity 365

keytool -exportcert -alias serverkey -keystore serverkeystore.jks -file servercert.cer

keytool -importcert -alias serverkey -file servercert.cer -keystore clienttruststore.jks


-alias clientkey: Tên alias cho key của client.
-keyalg RSA: Thuật toán mã hóa.
-keysize 2048: Độ dài key.
-keystore clientkeystore.jks: Tên file keystore sẽ tạo cho client.
-validity 365: Thời hạn chứng chỉ (ngày).


nc -l -p 1234 > servercert.cer

cat servercert.cer | nc <ipsent> 1234