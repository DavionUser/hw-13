import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpExercises {
    public static final String GET_USERS = "https://jsonplaceholder.typicode.com/users";
    public static final String GET_BY_ID = "https://jsonplaceholder.typicode.com/users/";
    public static final String GET_BY_USER_NAME = "https://jsonplaceholder.typicode.com/users?username=";
    public static final String GET_POSTS = "https://jsonplaceholder.typicode.com/users/%d/posts";
    public static final String GET_COMMENTS = "https://jsonplaceholder.typicode.com/posts/%d/comments";
    public static final String GET_TASKS = "https://jsonplaceholder.typicode.com/users/%d/todos";

    public static void main(String[] args) throws IOException, InterruptedException {
        User user = new User();
        User createdUser = HTTPUtils.post(user);
        System.out.println(createdUser);

        user.setName("Kostas");
        User updatedUser = HTTPUtils.put(user, 5);
        System.out.println(updatedUser);

        System.out.println(HTTPUtils.deleteUser(3));

        List<User> users = new ArrayList<>(HTTPUtils.getUsers());
        System.out.println(users);

        System.out.println(HTTPUtils.getById(3));

        System.out.println(HTTPUtils.getByUsername("Bret"));

        HTTPUtils.allCommentsToJson(1);

        System.out.println(HTTPUtils.openTasks(1));

    }
}

