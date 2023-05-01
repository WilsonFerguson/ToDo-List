import library.core.*;
import java.util.*;

class Day extends PComponent implements EventIgnorer {

    private Sketch sketch;

    String name;

    ArrayList<Category> categories = new ArrayList<Category>();

    int dayTextSize = 42;
    int categoryTextSize = parseInt(dayTextSize * 0.8);
    int assignmentTextSize = parseInt(categoryTextSize * 0.8);

    private int margin = 5;

    public Day(String name, Sketch sketch) {
        this.name = name;

        this.sketch = sketch;
    }

    private void addCategory(Category category) {
        categories.add(category);
    }

    public void load(String[] totalData) {
        // Clear the categories
        categories.clear();

        ArrayList<String> dayData = new ArrayList<>();

        // Go through the total data and find the data for this day
        for (String line : totalData) {
            if (line.equals("") || line.charAt(0) != '^' || !line.contains(name))
                continue;

            // Load the data until either a new day or the end of the file is reached
            for (int i = indexOf(totalData, line) + 1; i < totalData.length; i++) {
                if (totalData[i].contains("^"))
                    break;

                dayData.add(totalData[i]);
            }
        }

        // Go through the day data and create categories
        for (String line : dayData) {
            if (line.equals("") || line.charAt(0) != '%')
                continue;

            if (line.equals(""))
                continue;

            // Create a new category
            Category category = new Category(line.substring(1, line.length() - 1));
            addCategory(category);

            // Go until either the end of the dayData or the next category is reached
            for (int i = dayData.indexOf(line) + 1; i < dayData.size(); i++) {
                if (dayData.get(i).equals(""))
                    continue;

                if (dayData.get(i).charAt(0) == '%')
                    break;

                // Create a new assignment
                String assignment = dayData.get(i);
                String name = "";
                int duration = -1;
                if (assignment.contains("|")) {
                    name = assignment.substring(0, assignment.indexOf("|") - 1);
                    duration = parseInt(assignment.substring(assignment.indexOf("|") + 2));
                } else {
                    name = assignment;
                }
                category.addAssignment(name, duration);
            }
        }
    }

    public String[] save() {
        ArrayList<String> dayData = new ArrayList<String>();

        dayData.add("^" + name + "^");

        for (Category category : categories) {
            dayData.add("%" + category.getName() + "%");

            HashMap<String, Integer> assignments = category.getAssignments();
            for (String name : assignments.keySet()) {
                if (assignments.get(name) == -1)
                    dayData.add(name);
                else
                    dayData.add(name + " | " + assignments.get(name));
            }
            dayData.add("");
        }

        dayData.add("");

        return Helper.toStringArray(dayData);
    }

    public float getWidth() {
        textSize(dayTextSize);
        float maxW = textWidth(name);

        for (Category category : categories) {
            maxW = max(maxW, category.getWidth(categoryTextSize, assignmentTextSize));
        }

        return maxW;
    }

    public float getHeight() {
        textSize(dayTextSize);

        float h = textHeight(name);

        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);

            float categoryHeight = category.getHeight(categoryTextSize, assignmentTextSize);
            h += categoryHeight + 8;
        }

        return h;
    }

    public void draw() {
        textSize(dayTextSize);
        float h = textHeight(name);

        // Name background
        fill(sketch.nameColor);
        noStroke();
        rect(-margin, -h / 2, getWidth() + margin * 2, h);

        fill(sketch.textColor);
        text(name, 0, 0);

        float maxW = getWidth();

        translate(0, h / 2);

        translate(0, h / 2);
        noStroke();
        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);

            // Calculate h of the category
            // float categoryH = category.geth / (category.getAssignments().size() + 1);
            float categoryH = category.getHeight(categoryTextSize, assignmentTextSize);

            drawBackground(i, categoryH, maxW);

            category.draw(categoryTextSize, assignmentTextSize, sketch.textColor);

            translate(0, 10);
        }
        float dayHeight = getHeight() - h;

        // Draw border
        stroke(sketch.borderColor);
        translate(0, -(h + dayHeight + margin));
        dayHeight -= h / 2 - 3 - margin;
        line(-margin, -h / 2, maxW + margin, -h / 2);
        line(maxW + margin, -h / 2, maxW + margin, (h + dayHeight));
        line(maxW + margin, (h + dayHeight), -margin, (h + dayHeight));
        line(-margin, (h + dayHeight), -margin, -h / 2);
    }

    private void drawBackground(int i, float categoryHeight, float maxW) {
        color c = sketch.category1;
        i %= 3;
        switch (i) {
            case 0:
                c = sketch.category1;
                break;
            case 1:
                c = sketch.category2;
                break;
            case 2:
                c = sketch.category3;
                break;
        }
        fill(c);

        float rectX = -margin; // Account for margin on left

        textSize(dayTextSize);
        float rectY = -textHeight("A") / 2;

        float rectW = maxW + margin * 2; // Width + margin on both sides

        float rectH = categoryHeight;
        rectH += 10; // We are translate down 10 afterwards to draw the next
                     // category, so account for that
        rectH += 3; // Not sure why this is needed, but it is
        rect(rectX, rectY, rectW, rectH);
    }

}