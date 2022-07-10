import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import users_info.Comments;
import users_info.Posts;
import users_info.Tasks;
import users_info.User;

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
        int postsCount = getPosts(userId).size();
        ArrayList<Comments> comments = getComments(postsCount);

        File fileJson = new File(String.format("user-%d-post-%d-comments.json", userId, postsCount));
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
        ArrayList<Tasks> tasks = getTasks(userId);

        List<Tasks> uncompleted = new ArrayList<>();
        for (Tasks task: tasks) {
            if (!task.isCompleted()) {
                uncompleted.add(task);
            }
        }
        return uncompleted;
    }

    public static ArrayList<Posts> getPosts(int userId) throws IOException, InterruptedException {
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

        return posts;
    }

    public static ArrayList<Comments> getComments(int postId) throws IOException, InterruptedException {
        String getComments = String.format(HttpExercises.GET_COMMENTS, postId);
        HttpRequest requestComments = HttpRequest.newBuilder()
                .uri(URI.create(getComments))
                .GET()
                .build();

        HttpResponse<String> responseComments = CLIENT.send(requestComments, HttpResponse.BodyHandlers.ofString());
        System.out.println(responseComments.statusCode() + " getComments");
        Type commentsListType = new TypeToken<ArrayList<Comments>>() {
        }.getType();

        return GSON.fromJson(responseComments.body(), commentsListType);
    }

    public static ArrayList<Tasks> getTasks(int userId) throws IOException, InterruptedException {
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

        return tasks;
    }
}