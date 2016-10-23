import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        SearchFilterWrapper wrapper = new SearchFilterWrapper(args[0], args[1]);
        wrapper.getByCategory("Shop").forEach(System.out::println);
    }
}
