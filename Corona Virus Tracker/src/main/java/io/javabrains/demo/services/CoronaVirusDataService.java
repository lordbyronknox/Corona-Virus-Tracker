package io.javabrains.demo.services;

import io.javabrains.demo.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

//get data from file in GitHub (by making a http call to it's uri)
@Service    //makes this class a Spring service.
public class CoronaVirusDataService {

    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private List<LocationStats> allStats = new ArrayList<>();   //create a List to store instances of LocationStats

    @PostConstruct      //tells Spring to execute this method when its done constructing the instance of the service.
    //@Scheduled - schedules the run of a method. (we want the method to run every day to update the data)
    //add @EnableScheduling above the class with the main method.
    @Scheduled(cron = "* * 1 * * *")
    public void fetchVirusData() throws IOException, InterruptedException {
        List<LocationStats> newStats = new ArrayList<>();   //list to store new LocationStats objects in while parsing.
                                                            // This prevents errors for users accessing the 'allStats' list while parsing.
        HttpClient client = HttpClient.newHttpClient();     //httpclient is used to send requests.
        HttpRequest request = HttpRequest.newBuilder()      //begin building the request...
                .uri(URI.create(VIRUS_DATA_URL))            //create the uri by parsing the string "VIRUS_DATA_URL".
                .build();                                   //build and return the HttpRequest.

        //use the HttpClient 'client' to send the request.
        //(<request to send>, <how to handle the response - ie. take the body and return it as a string>)
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        //create a string reader object
        StringReader csvBodyReader = new StringReader(httpResponse.body());

        //parse the returned string.
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
        for (CSVRecord record : records) {
            LocationStats locationStat = new LocationStats();
            locationStat.setState(record.get("Province/State"));
            locationStat.setCountry(record.get("Country/"));

        }
    }
}
