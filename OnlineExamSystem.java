import java.util.*;
import java.util.concurrent.*;

public class OnlineExamSystem {
    private static final Map<String, String> users = new HashMap<>();
    private static String currentUser;
    private static final List<Question> questions = new ArrayList<>();
    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) {
        // Initialize users
        users.put("user1", "password1");
        
        // Initialize questions
        questions.add(new Question("What is 2 + 2?", Arrays.asList("3", "4", "5")));
        questions.add(new Question("What is the capital of France?", Arrays.asList("Berlin", "Madrid", "Paris")));
        
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("1. Login");
            System.out.println("2. Update Profile");
            System.out.println("3. Start Exam");
            System.out.println("4. Logout");
            System.out.println("5. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    login(scanner);
                    break;
                case 2:
                    updateProfile(scanner);
                    break;
                case 3:
                    if (currentUser != null) {
                        startExam(scanner);
                    } else {
                        System.out.println("Please login first.");
                    }
                    break;
                case 4:
                    logout();
                    break;
                case 5:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
        scanner.close();
        scheduler.shutdown();
    }

    private static void login(Scanner scanner) {
        System.out.println("Enter username:");
        String username = scanner.nextLine();
        System.out.println("Enter password:");
        String password = scanner.nextLine();

        if (users.containsKey(username) && users.get(username).equals(password)) {
            currentUser = username;
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    private static void updateProfile(Scanner scanner) {
        if (currentUser == null) {
            System.out.println("Please login first.");
            return;
        }

        System.out.println("Enter new password:");
        String newPassword = scanner.nextLine();
        users.put(currentUser, newPassword);
        System.out.println("Password updated successfully.");
    }

    private static void startExam(Scanner scanner) {
        if (questions.isEmpty()) {
            System.out.println("No questions available.");
            return;
        }

        System.out.println("You have 2 minutes to complete the exam.");
        final int examDuration = 2 * 60; // 2 minutes in seconds

        // Start timer
        final ScheduledFuture<?> timerHandle = scheduler.schedule(() -> {
            System.out.println("Time is up!");
            submitExam();
        }, examDuration, TimeUnit.SECONDS);

        // Collect answers
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            System.out.println((i + 1) + ". " + question.getQuestionText());
            for (int j = 0; j < question.getOptions().size(); j++) {
                System.out.println((j + 1) + ": " + question.getOptions().get(j));
            }
            System.out.println("Select an option:");
            int answer = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            // Here you would save the answer
        }

        // If exam completed before timer, cancel timer
        timerHandle.cancel(false);
        submitExam();
    }

    private static void submitExam() {
        System.out.println("Exam submitted.");
        // Process and evaluate the answers here
    }

    private static void logout() {
        currentUser = null;
        System.out.println("Logged out successfully.");
    }

    static class Question {
        private final String questionText;
        private final List<String> options;

        public Question(String questionText, List<String> options) {
            this.questionText = questionText;
            this.options = options;
        }

        public String getQuestionText() {
            return questionText;
        }

        public List<String> getOptions() {
            return options;
        }
    }
}