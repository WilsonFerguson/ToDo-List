import library.core.*;
import java.util.*;

public class Category extends PComponent implements EventIgnorer {

    private String name;

    // Homework assignments (name and duration)
    private HashMap<String, Integer> assignments = new LinkedHashMap<>(); // Linked so that the order is preserved

    public Category(String name) {
        this.name = name;
    }

    public void addAssignment(String name, int duration) {
        assignments.put(name, duration);
    }

    public void removeAssignment(String name) {
        assignments.remove(name);
    }

    public void addAssignment(String name) {
        addAssignment(name, -1);
    }

    public HashMap<String, Integer> getAssignments() {
        return assignments;
    }

    public String getName() {
        return name;
    }

    public float getHeight(int categoryTextSize, int assignmentTextSize) {
        float categoryHeight = 0;

        textSize(categoryTextSize);

        float h = textHeight(name);

        categoryHeight += h * 4 / 5;
        if (name.equals(""))
            categoryHeight -= h * 4 / 5;

        categoryHeight += assignments.size() * h * 4 / 5;

        return categoryHeight;
    }

    public float getWidth(int categoryTextSize, int assignmentTextSize) {
        textSize(categoryTextSize);
        float maxW = textWidth(name);

        textSize(assignmentTextSize);
        for (String assignmentName : assignments.keySet()) {
            String name = assignmentName;
            if (assignments.get(assignmentName) != -1)
                name += " (" + assignments.get(assignmentName) + " minutes)";
            float w = textWidth(name);
            w += categoryTextSize; // Indent

            if (name.equals(""))
                w -= categoryTextSize;

            maxW = max(maxW, w);
        }

        return maxW;
    }

    public void draw(int categoryTextSize, int assignmentTextSize, color textColor) {
        textSize(categoryTextSize);
        fill(textColor);
        noStroke();
        text(name, 0, 0);

        float h = textHeight(name);

        translate(categoryTextSize, h * 4 / 5);

        if (name.equals(""))
            translate(-categoryTextSize, -h * 4 / 5);

        textSize(assignmentTextSize);
        for (String assignmentName : assignments.keySet()) {
            String name = assignmentName;
            if (assignments.get(assignmentName) != -1)
                name += " (" + assignments.get(assignmentName) + " minutes)";
            text(name, 0, 0);

            translate(0, h * 4 / 5);
        }

        if (!name.equals(""))
            translate(-categoryTextSize, 0);
    }
}
