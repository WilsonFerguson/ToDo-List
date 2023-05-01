import library.core.*;
import java.util.*;
import GameEngine.*;

class Sketch extends Applet {

    ArrayList<Day> days = new ArrayList<>();

    int daysPerRow = 2;

    color backgroundColor = color(52, 54, 61); // 89, 145, 118
    color textColor = color(230);
    color borderColor = color(150);
    color nameColor = color(105, 111, 126);
    color category1 = color(73, 77, 88);
    color category2 = color(61, 64, 75);
    color category3 = color(48, 52, 62);

    Panel uiPanel;
    Button openTXTButton;
    Button saveToFileButton;
    Button loadChangesButton;
    double openTXTWidth, saveToFileWidth, loadChangesWidth, sizeMultiplier, textSizeMultiplier;

    String[] totalData;
    int loadFrequency = 1; // How many seconds to wait before loading data
    boolean loadAutomatically = true; // Whether or not to load data automatically

    private float scrollAmount = 0;
    private int targetScrollAmount = 0;

    public void setup() {
        // size(1920, 1200);
        size(1600, 1000);
        setTitle("To Do List");
        textFont("fonts/Consolas.ttf");
        setResizable(true);

        sizeMultiplier = 1.02;
        textSizeMultiplier = 1.05;

        textSize(35);
        openTXTWidth = textWidth("Open TXT") * textSizeMultiplier;
        saveToFileWidth = textWidth("Save to File") * textSizeMultiplier;
        loadChangesWidth = textWidth("Load Changes") * textSizeMultiplier;

        createButtons();
        animateButtons();

        setupDays();
        loadDays();
        saveToFile(); // Update text file's order
    }

    public void setupDays() {
        days.clear();

        String[] dayNames = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };

        // Order the days so that the current day is first
        String[] temp = new String[7];
        int index = 0;
        Calendar calendar = Calendar.getInstance();
        for (int i = 7 - 1; i >= 0; i--) {
            if (dayNames[i].equals(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US))) {
                index = i;
                break;
            }
        }

        for (int i = 0; i < 7; i++) {
            temp[i] = dayNames[index];
            index++;
            if (index >= 7)
                index = 0;
        }
        dayNames = temp;
        for (int i = 0; i < 7; i++) {
            days.add(new Day(dayNames[i], this));
        }

        String[] data = loadStrings("days.txt");
        // Go through each line and if it starts with a ^, then it's another day
        for (String line : data) {
            if (!line.startsWith("^"))
                continue;

            String dayName = line.substring(1, line.length() - 1);
            // If it's a day that we already have, then skip it
            if (Arrays.asList(dayNames).contains(dayName))
                continue;

            days.add(new Day(dayName, this));
        }
    }

    public void loadDays() {
        String[] newTotalData = loadStrings("days.txt");

        // If they are different, update totalData
        if (Arrays.equals(totalData, newTotalData))
            return;

        // Recreate the days (just in case they changed a day/category name)
        setupDays();

        totalData = newTotalData;

        for (Day day : days) {
            day.load(totalData);
        }

        saveToFileButton.text = "Save to File"; // Reset the text as something has changed
        saveToFileButton.interactive = true;
    }

    public void createButtons() {
        uiPanel = new Panel(width / 2, height - 65, 750, 110, color(backgroundColor, 200), borderColor);

        color defaultColor = color(backgroundColor.r - 8, backgroundColor.g - 8, backgroundColor.b - 8);
        color hoverColor = backgroundColor;
        color activeColor = color(backgroundColor.r - 10, backgroundColor.g - 10, backgroundColor.b - 10);
        Button.setDefaults(
                new Button(PVector.center(), PVector.zero(), defaultColor, hoverColor, activeColor, color(0, 0),
                        "", 0, textColor));

        int margin = 25;
        double totalWidth = openTXTWidth * sizeMultiplier + saveToFileWidth * sizeMultiplier
                + loadChangesWidth * sizeMultiplier + margin * 2;

        double loadChangesPosition = -totalWidth / 2 + loadChangesWidth * sizeMultiplier / 2;
        double openTXTPosition = totalWidth / 2 - openTXTWidth * sizeMultiplier / 2;
        double saveToFilePosition = loadChangesPosition + loadChangesWidth * sizeMultiplier / 2 + margin
                + saveToFileWidth
                        * sizeMultiplier / 2;

        loadChangesButton = new Button(loadChangesPosition, 0, 0, 0, "Load Changes").onClick(() -> loadDays());
        loadChangesButton.setTextSize(0);
        loadChangesButton.setCornerRadius(30);

        saveToFileButton = new Button(saveToFilePosition, 0, 0, 0, "Save to File").onClick(() -> {
            saveToFile();
            saveToFileButton.text = "Saved!";
            saveToFileButton.interactive = false;
        });
        saveToFileButton.setTextSize(0);
        saveToFileButton.setCornerRadius(30);

        openTXTButton = new Button(openTXTPosition, 0, 0, 0, "Open TXT").onClick(() -> openFile("days.txt"));
        openTXTButton.setTextSize(0);
        openTXTButton.setCornerRadius(30);

        uiPanel.addElements(openTXTButton, saveToFileButton, loadChangesButton);
    }

    public void animateButtons() {
        new Animator(openTXTButton, 0.25).setSize(openTXTWidth, 75).setTextSize(35).delay(0.1);
        new Animator(saveToFileButton, 0.25).setSize(saveToFileWidth, 75).setTextSize(35).delay(0.1);
        new Animator(loadChangesButton, 0.25).setSize(loadChangesWidth, 75).setTextSize(35).delay(0.1);

        loadChangesButton.onHover(() -> {
            new Animator(loadChangesButton, 0.1)
                    .setSize(loadChangesWidth * sizeMultiplier, 75 * sizeMultiplier)
                    .setTextSize(35 * sizeMultiplier);
        });
        loadChangesButton.onHoverExit(() -> {
            new Animator(loadChangesButton, 0.1).setSize(loadChangesWidth, 75)
                    .setTextSize(35);
        });

        openTXTButton.onHover(() -> {
            new Animator(openTXTButton, 0.1)
                    .setSize(openTXTWidth * sizeMultiplier, 75 * sizeMultiplier)
                    .setTextSize(35 * sizeMultiplier);
        });
        openTXTButton.onHoverExit(() -> {
            new Animator(openTXTButton, 0.1).setSize(openTXTWidth, 75).setTextSize(35);
        });

        saveToFileButton.onHover(() -> {
            if (!saveToFileButton.interactive)
                return;
            new Animator(saveToFileButton, 0.1)
                    .setSize(saveToFileWidth * sizeMultiplier,
                            75 * sizeMultiplier)
                    .setTextSize(35 * sizeMultiplier);
        });
        saveToFileButton.onHoverExit(() -> {
            new Animator(saveToFileButton, 0.1).setSize(saveToFileWidth, 75)
                    .setTextSize(35);
        });

    }

    public void saveToFile() {
        ArrayList<String> totalData = new ArrayList<String>();
        for (Day day : days) {
            totalData.addAll(Arrays.asList(day.save()));
        }
        totalData.remove(totalData.size() - 1);
        saveStrings(Helper.toStringArray(totalData), "days.txt");
    }

    public void draw() {
        GameEngine.Run();
        background(backgroundColor);

        // Reload days if the file has been changed
        if (loadAutomatically && frameCount % (loadFrequency * 60) == 0)
            loadDays();

        translate(75, 75);
        scrollAmount = lerp(scrollAmount, targetScrollAmount, 0.15);
        translate(0, -scrollAmount);
        float totalWidth = 0;
        float maxHeight = 0;
        for (int i = 0; i < days.size(); i++) {

            // See if the day can fit on the current row
            float w = days.get(i).getWidth();
            float h = days.get(i).getHeight();
            if (totalWidth + w > width - 150) {
                translate(-totalWidth, maxHeight + 50);
                totalWidth = 0;
                maxHeight = 0;
            }

            push();
            days.get(i).draw();
            pop();

            translate(w + 50, 0);
            totalWidth += w + 50;
            maxHeight = Math.max(maxHeight, h);

        }

        push();
        resetTranslation();
        uiPanel.draw();
        pop();
        // TODO if I remove the push and pop then it breaks the days UI (only happens if
        // uiPanel.draw() is called)
    }

    public void keyPressed() {
        if (keysPressed.contains("Ctrl") && keysPressed.contains("S"))
            loadDays();

        if (keysPressed.contains("Ctrl") && keysPressed.contains("W"))
            exit();

        if (key == 'o')
            openFile("days.txt");
    }

    public void mouseScrolled(int amount) {
        targetScrollAmount += amount * 15;
        targetScrollAmount = max(0, targetScrollAmount);
    }
}