package model;

import java.time.LocalDate;
import constants.Departments;
import constants.Status;
import utility.ValidationUtils;

/**
 * Represents an immutable data object for a student in the system.
 * * <p>
 * * This class encapsulates all information related to a single student, including personal
 * * details, contact information, and academic status. The class is immutable, meaning a
 * * Student object cannot be changed after it is created. Instead, "with" methods are
 * * provided to create new Student instances with updated values.
 * * <p>
 * * All data is validated within the constructor to ensure that a Student object can never
 * * exist in an invalid state.
 * *
 * * @author Anupam Kushwaha
 * * @version 1.0
 */
public record Student(int id, String name, String email, String phone, LocalDate DOB, String address,
                      Departments department, Status status) {
    /**
     * Constructs a new Student instance with the specified details.
     * <p>
     * This constructor performs validation on key fields (name, email, phone) before
     * initializing the object. If any validation fails, it throws an exception.
     *
     * @param id         The unique identifier for the student.
     * @param name       The student's full name. Must not be null or blank.
     * @param email      The student's email address. Must be a valid format.
     * @param phone      The student's phone number. Must be a valid format.
     * @param DOB        The student's date of birth.
     * @param address    The student's physical address.
     * @param department The student's academic department.
     * @param status     The student's current enrollment status.
     * @throws IllegalArgumentException if name, email, or phone number are invalid.
     */
    public Student(int id, String name, String email, String phone, LocalDate DOB, String address, Departments department, Status status) {
        this.id = id;
        if (!ValidationUtils.isValidName(name)) {
            throw new IllegalArgumentException("Invalid name format.");
        }
        this.name = name;

        if (!ValidationUtils.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format.");
        }

        this.email = ValidationUtils.normalizeEmail(email);

        if (!ValidationUtils.isValidPhone(phone)) {
            throw new IllegalArgumentException("Invalid phone number.");
        }
        this.phone = ValidationUtils.normalizePhone(phone);


        this.DOB = DOB;
        this.address = address;
        this.department = department;
        this.status = status;
    }


    /**
     * Creates a new Student instance with an updated name.
     *
     * @param newName The new name for the student.
     * @return A new Student object with the name changed.
     */
    public Student withName(String newName) {
        return new Student(
                this.id,
                newName,
                this.email,
                this.phone,
                this.DOB,
                this.address,
                this.department,
                this.status
        );
    }

    /**
     * Creates a new Student instance with an updated email.
     *
     * @param newEmail The new email for the student.
     * @return A new Student object with the email changed.
     */
    public Student withEmail(String newEmail) {
        return new Student(
                this.id,
                this.name,
                newEmail,
                this.phone,
                this.DOB,
                this.address,
                this.department,
                this.status
        );
    }

    /**
     * Creates a new Student instance with an updated Phone.
     *
     * @param newPhone The new Phone for the student.
     * @return A new Student object with the Phone no changed.
     */
    public Student withPhone(String newPhone) {
        return new Student(
                this.id,
                this.name,
                this.email,
                newPhone,
                this.DOB,
                this.address,
                this.department,
                this.status
        );
    }

    /**
     * Creates a new Student instance with an updated Address.
     *
     * @param newAddress The new address for the student.
     * @return A new Student object with the address changed.
     */
    public Student withAddress(String newAddress) {
        return new Student(
                this.id,
                this.name,
                this.email,
                this.phone,
                this.DOB,
                newAddress,
                this.department,
                this.status
        );
    }

    /**
     * Creates a new Student instance with an updated Department.
     *
     * @param newDepartment The new department for the student.
     * @return A new Student object with the department changed.
     */
    public Student withDepartment(Departments newDepartment) {
        return new Student(
                this.id,
                this.name,
                this.email,
                this.phone,
                this.DOB,
                this.address,
                newDepartment,
                this.status
        );
    }

    /**
     * Creates a new Student instance with an updated Status.
     *
     * @param newStatus The new Status for the student.
     * @return A new Student object with the status changed.
     */
    public Student withStatus(Status newStatus) {
        return new Student(
                this.id,
                this.name,
                this.email,
                this.phone,
                this.DOB,
                this.address,
                this.department,
                newStatus
        );
    }

    /**
     * {@inheritDoc}
     * <p>
     * Two Student objects are considered equal if they have the same ID.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student s)) return false;
        return this.id == s.id;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The hash code is based solely on the student's unique ID.
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    /**
     * Returns a string representation of the student's core details.
     *
     * @return A formatted string including name, ID, contact info, department and status.
     */
    @Override
    public String toString() {

        return "\nName: " + name + "\nid: " + id + "\nphone: " + phone + "\nemail: " + email +
                "\nDOB: " + DOB + "\ndepartment: " + department + "\naddress: " + address;
    }

}
