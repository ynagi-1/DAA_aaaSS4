package smartcity.model;


public class Vertex {
    private final int id;
    private String name;
    private String taskType;
    private double duration;
    private int priority;

    public Vertex(int id) {
        this.id = id;
        this.name = "Task_" + id;
        this.taskType = "general";
        this.duration = 1.0;
        this.priority = 1;
    }

    public Vertex(int id, String name, String taskType, double duration, int priority) {
        this.id = id;
        this.name = name;
        this.taskType = taskType;
        this.duration = duration;
        this.priority = priority;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getTaskType() { return taskType; }
    public double getDuration() { return duration; }
    public int getPriority() { return priority; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    public void setDuration(double duration) { this.duration = duration; }
    public void setPriority(int priority) { this.priority = priority; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return id == vertex.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("Vertex{id=%d, name='%s', type='%s', duration=%.1f, priority=%d}",
                id, name, taskType, duration, priority);
    }


    public static Vertex createStreetCleaning(int id, double duration) {
        return new Vertex(id, "StreetClean_" + id, "street_cleaning", duration, 2);
    }


    public static Vertex createRepair(int id, double duration) {
        return new Vertex(id, "Repair_" + id, "repair", duration, 3);
    }


    public static Vertex createMaintenance(int id, double duration) {
        return new Vertex(id, "Maintenance_" + id, "maintenance", duration, 1);
    }


    public static Vertex createAnalytics(int id, double duration) {
        return new Vertex(id, "Analytics_" + id, "analytics", duration, 1);
    }
}