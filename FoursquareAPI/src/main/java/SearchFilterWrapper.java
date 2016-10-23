import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class SearchFilterWrapper {
    private final VenueSearcher searcher;

    private String longitude = "34.057";
    private String latitude = "-118.238";

    public void setCoordinates(String longitude, String latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public SearchFilterWrapper(String clientID, String clientSecret) {
        this.searcher = new VenueSearcher(clientID, clientSecret);
    }

    public List<FoursquareVenue> getByCategory(String categorySequence) throws IOException {
        return searcher.call(longitude, latitude)
                .stream()
                .filter(v -> v.getCategory().contains(categorySequence))
                .collect(Collectors.toList());
    }

    public List<FoursquareVenue> getByName(String nameSequence) throws IOException {
        return searcher.call(longitude, latitude)
                .stream()
                .filter(v -> v.getName().contains(nameSequence))
                .collect(Collectors.toList());
    }

    public List<FoursquareVenue> getByCity(String cityNameSequence) throws IOException {
        return searcher.call(longitude, latitude)
                .stream()
                .filter(v -> v.getCity().contains(cityNameSequence))
                .collect(Collectors.toList());
    }

    public List<FoursquareVenue> getByFacebookName(String cityNameSequence) throws IOException {
        return searcher.call(longitude, latitude)
                .stream()
                .filter(v -> v.getFacebookName().contains(cityNameSequence))
                .collect(Collectors.toList());
    }

    public List<FoursquareVenue> getByFacebook(String cityNameSequence) throws IOException {
        return searcher.call(longitude, latitude)
                .stream()
                .filter(v -> v.getFacebook().contains(cityNameSequence))
                .collect(Collectors.toList());
    }

    public List<FoursquareVenue> getByTwitterName(String cityNameSequence) throws IOException {
        return searcher.call(longitude, latitude)
                .stream()
                .filter(v -> v.getTwitterName().contains(cityNameSequence))
                .collect(Collectors.toList());
    }

    public List<FoursquareVenue> getByTwitter(String cityNameSequence) throws IOException {
        return searcher.call(longitude, latitude)
                .stream()
                .filter(v -> v.getTwitter().contains(cityNameSequence))
                .collect(Collectors.toList());
    }

    public List<FoursquareVenue> getByPhone(String cityNameSequence) throws IOException {
        return searcher.call(longitude, latitude)
                .stream()
                .filter(v -> v.getPhone().contains(cityNameSequence))
                .collect(Collectors.toList());
    }
    public List<FoursquareVenue> getAll() throws IOException {
        return searcher.call(longitude, latitude);
    }
}
