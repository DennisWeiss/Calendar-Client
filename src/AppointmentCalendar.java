import java.io.Serializable;

public class AppointmentCalendar implements Serializable {
    boolean[][] free = new boolean[5][10];

    AppointmentCalendar() {
        for (int i = 0; i < free.length; i++) {
            for (int j = 0; j < free[0].length; j++) {
                free[i][j] = true;
            }
        }
    }
}
