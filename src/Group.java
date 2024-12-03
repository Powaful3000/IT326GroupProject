import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Group {
    private int id;
    private String name;
    private String description;
    private Date creationDate;
    private List<Student> members;
    private Map<Integer, Date> memberJoinDates; // StudentID -> JoinDate
    private Map<Integer, Date> memberEndDates; // StudentID -> EndDate

    public Group(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.creationDate = new Date(); // Current date/time
        this.members = new ArrayList<>();
        this.memberJoinDates = new HashMap<>();
        this.memberEndDates = new HashMap<>();
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public List<Student> getMembers() {
        return new ArrayList<>(members);
    }

    public List<Student> getActiveMembers() {
        List<Student> activeMembers = new ArrayList<>();
        for (Student member : members) {
            if (!memberEndDates.containsKey(member.getID())) {
                activeMembers.add(member);
            }
        }
        return activeMembers;
    }

    public boolean addMember(Student student) {
        if (student == null || members.contains(student)) {
            return false;
        }
        members.add(student);
        memberJoinDates.put(student.getID(), new Date());
        return true;
    }

    public boolean removeMember(Student student) {
        if (student == null || !members.contains(student)) {
            return false;
        }
        memberEndDates.put(student.getID(), new Date());
        return true;
    }

    public Date getMemberJoinDate(Student student) {
        if (student == null || !members.contains(student)) {
            return null;
        }
        return memberJoinDates.get(student.getID());
    }

    public Date getMemberEndDate(Student student) {
        if (student == null || !members.contains(student)) {
            return null;
        }
        return memberEndDates.get(student.getID());
    }

    public boolean setMemberEndDate(Student student, Date endDate) {
        if (student == null || !members.contains(student)) {
            return false;
        }
        if (endDate == null) {
            memberEndDates.remove(student.getID());
        } else {
            memberEndDates.put(student.getID(), endDate);
        }
        return true;
    }

    public boolean isMemberActive(Student student) {
        if (student == null || !members.contains(student)) {
            return false;
        }
        return !memberEndDates.containsKey(student.getID());
    }

    public void setMemberJoinDate(Student student, Date joinDate) {
        if (student != null && members.contains(student)) {
            memberJoinDates.put(student.getID(), joinDate);
        }
    }

    public int getSize() {
        return members.size();
    }

    public boolean isMember(Student student) {
        return members.stream()
                .anyMatch(member -> member.getID() == student.getID());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Group other = (Group) obj;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return name + " (" + members.size() + " members)";
    }
}
