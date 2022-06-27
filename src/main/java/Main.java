import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Apod;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


import java.io.*;

import java.net.URI;
import java.net.URISyntaxException;


public class Main {

    private static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        // Создадим директорию для хранения
        File contentPath = new File("NASA_Content");
        contentPath.mkdir();

        //Создаем http клиента и метод запроса на сервер
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            URI uri = new URI("https://api.nasa.gov/planetary/apod?api_key=6KA4hbR2eB9hpANJsRQcIzsgFJpeKS4sV0Ud5ZG1");
            HttpUriRequest httpGet = new HttpGet(uri);

            //Переменные для храниня данных с ответа сервера
            URI contentUri;
            String fileName;
            //Инициализируем запрос на сервер, передавая клиенту нужный метод
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                //Получаем тело ответа (json)
                HttpEntity entity = response.getEntity();
                //Преобразуем в объект с помощью jackson
                Apod apod = mapper.readValue(entity.getContent(), new TypeReference<>() {
                });

                //Инициализируем переменные для храниния данных с ответа сервера
                String[] separatedUrl = apod.getUrl().split("/");
                fileName = separatedUrl[separatedUrl.length - 1];
                contentUri = URI.create(apod.getUrl());
            }

            //Инициализируем запрос на сервер, передавая клиенту нужный метод
            HttpUriRequest httpGetContent = new HttpGet(contentUri);
            try (CloseableHttpResponse response = httpClient.execute(httpGetContent)) {
                HttpEntity entity = response.getEntity();

                //Создаем файл в который будем запиывать полученные данные
                File content = new File("NASA_Content/" + fileName);
                //Создаем выходной поток и записываем данные
                OutputStream out = new FileOutputStream(content);
                entity.writeTo(out);
                out.close();
            }

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
