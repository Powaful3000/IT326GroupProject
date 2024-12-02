import java.util.Date;

public class Membership {

    // Attributes
    private Student student;
    private Group group;
    private Date joinDate;
    private Date endDate;

    // Constructor
    public Membership(Student student, Group group, Date joinDate, Date endDate) {
        if (student == null || group == null) {
            throw new IllegalArgumentException("Student and Group cannot be null.");
        }
        if (joinDate == null) {
            throw new IllegalArgumentException("Join date cannot be null.");
        }
        this.student = student;
        this.group = group;
        this.joinDate = joinDate;
        this.endDate = endDate;
    }

    // Getters
    public Student getStudent() {
        return student;
    }

    public Group getGroup() {
        return group;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    // Setters
    public void setStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null.");
        }
        this.student = student;
    }

    public void setGroup(Group group) {
        if (group == null) {
            throw new IllegalArgumentException("Group cannot be null.");
        }
        this.group = group;
    }

    public void setJoinDate(Date joinDate) {
        if (joinDate == null) {
            throw new IllegalArgumentException("Join date cannot be null.");
        }
        this.joinDate = joinDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    // Check if the membership is active
    public boolean isActive() {
        return endDate == null || endDate.after(new Date());
    }

    // Print membership details
    public void printDetails() {
        System.out.println("Membership Details:");
        System.out.println("Student: " + student.getName());
        System.out.println("Group: " + group.getName());
        System.out.println("Join Date: " + joinDate);
        System.out.println("End Date: " + (endDate != null ? endDate : "Active"));
    }
}
