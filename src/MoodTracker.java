import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class MoodTracker {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
        ArrayList<Mood> moodsList = new ArrayList<>();

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("Press 'a' to add mood");
                System.out.println("'d' to delete mood(s)");
                System.out.println("'e' to edit mood");
                System.out.println("'s' to search for moods");
                System.out.println("'M' to get all moods");
                System.out.println("'w' to write the moods to a file");
                System.out.println("Type 'Exit' to exit");

                String menuOption = scanner.nextLine();

                if (menuOption.equalsIgnoreCase("Exit")) {
                    System.out.println("Thank you for using the MoodTracker. Goodbye!");
                    return;
                }

                switch (menuOption) {
                    case "a":
                        addMood(scanner, moodsList);
                        continue;
                    case "d":
                        deleteMoodFlow(scanner, moodsList);
                        continue;
                    case "e":
                        editMoodFlow(scanner, moodsList);
                        continue;
                    case "s":
                        searchMoodFlow(scanner, moodsList);
                        continue;
                    case "M":
                        printAllMoods(moodsList);
                        continue;
                    case "w":
                        writeMoodsToFile(moodsList);
                        continue;
                    default:
                        System.out.println("Not a valid input!");
                }
            }
        }
    }

    public static boolean isMoodValid(Mood mood, ArrayList<Mood> moodsList) throws InvalidMoodException {
        for (Mood tempMood : moodsList) {
            if (tempMood.equals(mood)) {
                throw new InvalidMoodException();
            }
        }
        return true;
    }

    public static boolean deleteMoods(LocalDate moodDate, ArrayList<Mood> moodsList) {
        boolean removed = false;
        Iterator<Mood> iterator = moodsList.iterator();

        while (iterator.hasNext()) {
            Mood tempMood = iterator.next();
            if (tempMood.getDate().equals(moodDate)) {
                iterator.remove();
                removed = true;
            }
        }

        return removed;
    }

    public static boolean deleteMood(Mood mood, ArrayList<Mood> moodsList) {
        Iterator<Mood> iterator = moodsList.iterator();

        while (iterator.hasNext()) {
            Mood tempMood = iterator.next();
            if (tempMood.equals(mood)) {
                iterator.remove();
                return true;
            }
        }

        return false;
    }

    public static boolean editMood(Mood moodToEdit, ArrayList<Mood> moodsList) {
        for (Mood tempMood : moodsList) {
            if (tempMood.equals(moodToEdit)) {
                tempMood.setNotes(moodToEdit.getNotes());
                return true;
            }
        }

        return false;
    }

    public static void searchMoods(LocalDate moodDate, ArrayList<Mood> moodsList) {
        boolean found = false;

        for (Mood tempMood : moodsList) {
            if (tempMood.getDate().equals(moodDate)) {
                found = true;
                System.out.println(tempMood);
            }
        }

        if (!found) {
            System.out.println("No matching records could be found!");
        }
    }

    public static void searchMood(Mood mood, ArrayList<Mood> moodsList) {
        boolean found = false;

        for (Mood tempMood : moodsList) {
            if (tempMood.equals(mood)) {
                found = true;
                System.out.println(tempMood);
            }
        }

        if (!found) {
            System.out.println("No matching records could be found!");
        }
    }

    private static void addMood(Scanner scanner, ArrayList<Mood> moodsList) {
        System.out.println("Enter the mood name");
        String moodName = scanner.nextLine();
        System.out.println("Are you tracking the mood for a current day? y/n");
        String isForCurrentDate = scanner.nextLine();
        Mood moodToAdd;

        if (isForCurrentDate.equalsIgnoreCase("n")) {
            try {
                System.out.println("Input the date in MM/dd/yyyy format:");
                String moodDateStr = scanner.nextLine();
                LocalDate moodDate = LocalDate.parse(moodDateStr, DATE_FORMATTER);

                System.out.println("Input the time in HH:mm:ss format:");
                String moodTimeStr = scanner.nextLine();
                LocalTime moodTime = LocalTime.parse(moodTimeStr, TIME_FORMATTER);

                System.out.println("Add notes about this mood");
                String moodNotes = scanner.nextLine();

                if (moodNotes.strip().isEmpty()) {
                    moodToAdd = new Mood(moodName, moodDate, moodTime);
                } else {
                    moodToAdd = new Mood(moodName, moodDate, moodTime, moodNotes);
                }
            } catch (DateTimeParseException dfe) {
                System.out.println("Incorrect format of date or time. Cannot create mood.");
                return;
            }
        } else {
            System.out.println("Add notes about this mood");
            String moodNotes = scanner.nextLine();
            if (moodNotes.strip().isEmpty()) {
                moodToAdd = new Mood(moodName);
            } else {
                moodToAdd = new Mood(moodName, moodNotes);
            }
        }

        try {
            if (isMoodValid(moodToAdd, moodsList)) {
                moodsList.add(moodToAdd);
                System.out.println("The mood has been added to the tracker");
            }
        } catch (InvalidMoodException ime) {
            System.out.println("The mood is not valid");
        }
    }

    private static void deleteMoodFlow(Scanner scanner, ArrayList<Mood> moodsList) {
        System.out.println("Enter '1' to delete all moods by date");
        System.out.println("Enter '2' to delete a specific mood");
        String deleteVariant = scanner.nextLine();

        if (deleteVariant.equals("1")) {
            try {
                System.out.println("Input the date in MM/dd/yyyy format:");
                String moodDateStr = scanner.nextLine();
                LocalDate moodDate = LocalDate.parse(moodDateStr, DATE_FORMATTER);

                boolean areMoodsDeleted = deleteMoods(moodDate, moodsList);
                if (areMoodsDeleted) {
                    System.out.println("The moods have been deleted");
                } else {
                    System.out.println("No matching moods found");
                }
            } catch (DateTimeParseException dfe) {
                System.out.println("Incorrect format of date. Cannot delete mood.");
            }
        } else if (deleteVariant.equals("2")) {
            try {
                System.out.println("Enter the mood name");
                String moodName = scanner.nextLine();

                System.out.println("Input the date in MM/dd/yyyy format:");
                String moodDateStr = scanner.nextLine();
                LocalDate moodDate = LocalDate.parse(moodDateStr, DATE_FORMATTER);

                System.out.println("Input the time in HH:mm:ss format:");
                String moodTimeStr = scanner.nextLine();
                LocalTime moodTime = LocalTime.parse(moodTimeStr, TIME_FORMATTER);

                Mood delMood = new Mood(moodName, moodDate, moodTime);
                boolean isMoodDeleted = deleteMood(delMood, moodsList);
                if (isMoodDeleted) {
                    System.out.println("The mood has been deleted");
                } else {
                    System.out.println("No matching mood found");
                }
            } catch (DateTimeParseException dfe) {
                System.out.println("Incorrect format of date or time. Cannot delete mood.");
            }
        } else {
            System.out.println("Not a valid input!");
        }
    }

    private static void editMoodFlow(Scanner scanner, ArrayList<Mood> moodsList) {
        try {
            System.out.println("Enter the mood name");
            String moodName = scanner.nextLine();

            System.out.println("Input the date in MM/dd/yyyy format:");
            String moodDateStr = scanner.nextLine();
            LocalDate moodDate = LocalDate.parse(moodDateStr, DATE_FORMATTER);

            System.out.println("Input the time in HH:mm:ss format:");
            String moodTimeStr = scanner.nextLine();
            LocalTime moodTime = LocalTime.parse(moodTimeStr, TIME_FORMATTER);

            System.out.println("Add new notes about this mood");
            String moodNotes = scanner.nextLine();

            if (moodNotes.strip().isEmpty()) {
                System.out.println("No notes entered");
                return;
            }

            Mood moodToEdit = new Mood(moodName, moodDate, moodTime, moodNotes);
            boolean isMoodEdited = editMood(moodToEdit, moodsList);
            if (isMoodEdited) {
                System.out.println("The mood has been successfully edited");
            } else {
                System.out.println("No matching mood could be found");
            }
        } catch (DateTimeParseException dfe) {
            System.out.println("Incorrect format of date or time. Cannot create mood.");
        }
    }

    private static void searchMoodFlow(Scanner scanner, ArrayList<Mood> moodsList) {
        System.out.println("Enter '1' to search for all moods by date");
        System.out.println("Enter '2' to search for a specific mood");
        String searchVariant = scanner.nextLine();

        if (searchVariant.equals("1")) {
            try {
                System.out.println("Input the date in MM/dd/yyyy format:");
                String moodDateStr = scanner.nextLine();
                LocalDate moodDate = LocalDate.parse(moodDateStr, DATE_FORMATTER);
                searchMoods(moodDate, moodsList);
            } catch (DateTimeParseException dfe) {
                System.out.println("Incorrect format of date. Cannot search mood.");
            }
        } else if (searchVariant.equals("2")) {
            try {
                System.out.println("Enter the mood name");
                String moodName = scanner.nextLine();

                System.out.println("Input the date in MM/dd/yyyy format:");
                String moodDateStr = scanner.nextLine();
                LocalDate moodDate = LocalDate.parse(moodDateStr, DATE_FORMATTER);

                System.out.println("Input the time in HH:mm:ss format:");
                String moodTimeStr = scanner.nextLine();
                LocalTime moodTime = LocalTime.parse(moodTimeStr, TIME_FORMATTER);

                Mood foundMood = new Mood(moodName, moodDate, moodTime);
                searchMood(foundMood, moodsList);
            } catch (DateTimeParseException dfe) {
                System.out.println("Incorrect format of date or time. Cannot search mood.");
            }
        } else {
            System.out.println("Not a valid input!");
        }
    }

    private static void printAllMoods(ArrayList<Mood> moodsList) {
        if (moodsList.isEmpty()) {
            System.out.println("No moods tracked yet.");
            return;
        }

        for (Mood moodObj : moodsList) {
            System.out.println(moodObj);
        }
    }

    private static void writeMoodsToFile(ArrayList<Mood> moodsList) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("moodtracker.txt"))) {
            for (Mood mood : moodsList) {
                writer.println(mood);
                writer.println();
            }
            System.out.println("The entries are written to a file");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
