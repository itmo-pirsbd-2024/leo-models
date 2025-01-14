# Сервис инференса моделей
Сервис предоставляет удобную возможность запускать модели на заданном наборе фичей и получать предсказания.

# Апи
Апи сервиса состоит из 3х ручек:
1) ModelsService.LoadModel - загружает заданную модель из подключенного Minio.
2) ModelsService.ModelList - показывает список загруженных моделей, доступных для инференса.
3) ModelsService.ModelInference - получить предсказания для заданных векторов фичей.

# Запуск dev окружения
Соберите приложение:
```shell
./gradlew build
./gradlew shadowJar
```

Запустите minio:
```shell
cd dev
docker compose up
```

Запустите jar:
```shell
java -jar ./build/libs/leo-models-1.0-SNAPSHOT-all.jar
```

По [адресу](http://127.0.0.1:9001/browser/leo-models) можно залить модель в minio.
Через grpcui можно пользоваться приложением:
```shell
./grpcui -plaintext  localhost:8080
```
