package utility;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import constants.Departments;
import constants.Status;
import model.Student;

import java.io.IOException;
import java.time.LocalDate;
/**
 * A custom Gson {@link TypeAdapter} for the immutable {@link Student} class.
 * <p>
 * This adapter is necessary because the {@code Student} class is immutable and lacks a
 * no-argument constructor, which Gson's default reflection-based mechanism requires.
 * This class provides explicit, low-level instructions on how to write a {@code Student}
 * object to JSON and how to read JSON to construct a new {@code Student} object.
 * By using the full constructor of the {@code Student} class during deserialization,
 * it ensures that all business-level validation logic is enforced.
 */
public class StudentTypeAdapter extends TypeAdapter<Student> {
    /**
     * Writes a {@link Student} object to a JSON stream field by field.
     *
     * @param out     The {@link JsonWriter} to write the JSON output to.
     * @param student The {@link Student} object to serialize.
     * @throws IOException if an error occurs during writing to the stream.
     */
    @Override
    public void write(JsonWriter out, Student student) throws IOException {
        out.beginObject();
        out.name("id").value(student.getId());
        out.name("name").value(student.getName());
        out.name("email").value(student.getEmail());
        out.name("phone").value(student.getPhone());
        out.name("DOB").value(student.getDOB().toString());
        out.name("address").value(student.getAddress());
        out.name("department").value(student.getDepartment().name());
        out.name("status").value(student.getStatus().name());
        out.endObject();
    }
    /**
     * Reads a JSON object from a stream and constructs a new {@link Student} object.
     * <p>
     * This method manually reads each field from the JSON object, handling potential
     * null values. It then uses the data to call the all-arguments constructor of the
     * {@code Student} class, thereby leveraging the built-in validation.
     *
     * @param in The {@link JsonReader} to read the JSON input from.
     * @return A new, fully constructed and validated {@link Student} object.
     * @throws IOException if an error occurs during reading from the stream.
     */
    @Override
    public Student read(JsonReader in) throws IOException {
        in.beginObject();

        // Temporary variables to hold the data as we read it
        int id = 0;
        String name = null;
        String email = null;
        String phone = null;
        LocalDate dob = null;
        String address = null;
        Departments department = null;
        Status status = null;

        while (in.hasNext()) {
            String fieldName = in.nextName();
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                continue;
            }
            switch (fieldName) {
                case "id":
                    id = in.nextInt();
                    break;
                case "name":
                    name = in.nextString();
                    break;
                case "email":
                    email = in.nextString();
                    break;
                case "phone":
                    phone = in.nextString();
                    break;
                case "DOB":
                    dob = LocalDate.parse(in.nextString());
                    break;
                case "address":
                    address = in.nextString();
                    break;
                case "department":
                    department = Departments.valueOf(in.nextString());
                    break;
                case "status":
                    status = Status.valueOf(in.nextString());
                    break;
                default:
                    in.skipValue(); // Ignore any unknown fields
                    break;
            }
        }
        in.endObject();

        // Call the full constructor, which includes all your validation logic
        return new Student(id, name, email, phone, dob, address, department, status);
    }
}
