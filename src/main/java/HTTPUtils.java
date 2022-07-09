import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class HTTPUtils {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new Gson();
    public static String json;

    public static User post(User user) throws IOException, InterruptedException {
        String userJson = GSON.toJson(user);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(HttpExercises.GET_USERS))
                .POST(HttpRequest.BodyPublishers.ofString(userJson))
                .header("Content-type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.statusCode() + " post");
        return GSON.fromJson(response.body(), User.class);
    }

    public static User put(User user, long id) throws IOException, InterruptedException {
        user.setName("Kostas");
        String jsonUser = GSON.toJson(user);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(HttpExercises.GET_BY_ID + id))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonUser))
                .header("Content-type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.statusCode() + " put");
        return GSON.fromJson(response.body(), User.class);
    }

    public static boolean deleteUser(long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(HttpExercises.GET_BY_ID + id))
                .DELETE()
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("delete");
        return response.statusCode() == 200;
    }

    public static Collection<User> getUsers() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(HttpExercises.GET_USERS))
                .GET()
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        Type collectionType = new TypeToken<ArrayList<User>>() {
        }.getType();
        System.out.println(response.statusCode() + " getUsers");
        return GSON.fromJson(response.body(), collectionType);
    }

    public static User getById(long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(HttpExercises.GET_BY_ID + id))
                .GET()
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.statusCode() + " getById");
        return GSON.fromJson(response.body(), User.class);
    }

    public static User getByUsername(String username) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(HttpExercises.GET_BY_USER_NAME + username))
                .GET()
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        Type userListType = new TypeToken<ArrayList<User>>() {
        }.getType();
        ArrayList<User> users = GSON.fromJson(response.body(), userListType);

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                System.out.println(response.statusCode() + " getByUsername");
                return user;
            }
        }
        return null;
    }

    public static void allCommentsToJson(int userId) throws IOException, InterruptedException {
        String getPosts = String.format(HttpExercises.GET_POSTS, userId);
        HttpRequest requestPosts = HttpRequest.newBuilder()
                .uri(URI.create(getPosts))
                .GET()
                .build();

        HttpResponse<String> responsePosts = CLIENT.send(requestPosts, HttpResponse.BodyHandlers.ofString());
        System.out.println(responsePosts.statusCode() + " getPosts");
        Type postsListType = new TypeToken<ArrayList<Posts>>() {
        }.getType();
        ArrayList<Posts> posts = GSON.fromJson(responsePosts.body(), postsListType);
        System.out.println(posts.size() + " size");

        String getComments = String.format(HttpExercises.GET_COMMENTS, posts.size());
        HttpRequest requestComments = HttpRequest.newBuilder()
                .uri(URI.create(getComments))
                .GET()
                .build();

        HttpResponse<String> responseComments = CLIENT.send(requestComments, HttpResponse.BodyHandlers.ofString());
        System.out.println(responseComments.statusCode() + " getComments");
        Type commentsListType = new TypeToken<ArrayList<Comments>>() {
        }.getType();
        ArrayList<Comments> comments = GSON.fromJson(responseComments.body(), commentsListType);

        File fileJson = new File(String.format("user-%d-post-%d-comments.json", userId, posts.size()));
        try (BufferedWriter bWriter = new BufferedWriter(new FileWriter(fileJson))) {
            Gson gson = new Gson();
            json = gson.toJson(comments);
            bWriter.write(json);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        System.out.println("Json file is written");
//        "user-X-post-Y-comments.json", где Х - номер пользователя, Y - номер поста.
    }

    public static List<Tasks> openTasks(int userId) throws IOException, InterruptedException {
        String getTasks = String.format(HttpExercises.GET_TASKS, userId);
        HttpRequest requestPosts = HttpRequest.newBuilder()
                .uri(URI.create(getTasks))
                .GET()
                .build();

        HttpResponse<String> responsePosts = CLIENT.send(requestPosts, HttpResponse.BodyHandlers.ofString());
        System.out.println(responsePosts.statusCode() + " getTasks");
        Type tasksListType = new TypeToken<ArrayList<Tasks>>() {}.getType();
        ArrayList<Tasks> tasks = GSON.fromJson(responsePosts.body(), tasksListType);
        System.out.println(tasks.size() + " tasks size");

        List<Tasks> uncompleted = new ArrayList<>();
        for (Tasks task: tasks) {
            if (!task.isCompleted()) {
                uncompleted.add(task);
            }
        }
        return uncompleted;
    }
}

class User {
    public int id;
    public String name;
    public String username;
    public String email;
    public Address address;
    public String phone;
    public String website;
    public Company company;

    public User() {
    }

    public User(int id, String name, String username, String email, Address address, String phone, String website,
                Company company) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.website = website;
        this.company = company;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", address=" + address +
                ", phone='" + phone + '\'' +
                ", website='" + website + '\'' +
                ", company=" + company +
                '}';
    }
}

class Address {
    public String street;
    public String suite;
    public String city;
    public String zipcode;
    public Geo geo;

    public Address(String street, String suite, String city, String zipcode, Geo geo) {
        this.street = street;
        this.suite = suite;
        this.city = city;
        this.zipcode = zipcode;
        this.geo = geo;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getSuite() {
        return suite;
    }

    public void setSuite(String suite) {
        this.suite = suite;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public Geo getGeo() {
        return geo;
    }

    public void setGeo(Geo geo) {
        this.geo = geo;
    }

    @Override
    public String toString() {
        return "Address{" +
                "street='" + street + '\'' +
                ", suite='" + suite + '\'' +
                ", city='" + city + '\'' +
                ", zipcode='" + zipcode + '\'' +
                ", geo=" + geo +
                '}';
    }
}

class Company {
    public String name;
    public String catchPhrase;
    public String bs;

    public Company(String name, String catchPhrase, String bs) {
        this.name = name;
        this.catchPhrase = catchPhrase;
        this.bs = bs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCatchPhrase() {
        return catchPhrase;
    }

    public void setCatchPhrase(String catchPhrase) {
        this.catchPhrase = catchPhrase;
    }

    public String getBs() {
        return bs;
    }

    public void setBs(String bs) {
        this.bs = bs;
    }

    @Override
    public String toString() {
        return "Company{" +
                "name='" + name + '\'' +
                ", catchPhrase='" + catchPhrase + '\'' +
                ", bs='" + bs + '\'' +
                '}';
    }
}

class Geo {
    public String lat;
    public String lng;

    public Geo(String lat, String lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "Geo{" +
                "lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                '}';
    }
}

class Posts {
    private int userId;
    private int id;
    private String title;
    private String body;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Posts{" +
                "userId=" + userId +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}

class Comments {
    private int postId;
    private int id;
    private String name;
    private String email;
    private String body;

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Comments{" +
                "postId=" + postId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}

class Tasks {
    private int userId;
    private int id;
    private String title;
    private boolean completed;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        return "ToDos{" +
                "userId=" + userId +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", completed=" + completed +
                '}';
    }
}