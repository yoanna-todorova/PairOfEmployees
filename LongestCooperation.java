import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LongestCooperation {

	private static final String NULL = "NULL";

	public static void main(String[] args) {

		List<Employee> employees = readEmployeesFromCSV("C:\\Users\\ytodorova5\\SirmaTask\\SirmaTest.csv");
		// System.out.println(employees.get(1).getDateTo());

		List<Cooperation> cooperations = saveCooperationInformation(employees);
		Cooperation longestCooperation = findMaxCooperation(cooperations);
		System.out.println("Employees with ids: " + longestCooperation.getEmployeeId1() + " and "
				+ longestCooperation.getEmployeeId2() + " have worked together for "
				+ longestCooperation.getDurationCooperationInDays() + " days.");
	}

	private static List<Employee> readEmployeesFromCSV(String fileName) {
		List<Employee> employees = new ArrayList<>();
		Path pathToFile = Paths.get(fileName);

		try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.US_ASCII)) {

			String line = br.readLine();

			while (line != null) {

				String[] attributes = line.split(",");

				Employee employee = createEmployeeRecord(attributes);

				employees.add(employee);

				line = br.readLine();
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return employees;
	}

	// Finds the maximum number(days) amongst all the cooperations that occurred
	private static Cooperation findMaxCooperation(List<Cooperation> cooperations) {
		Cooperation max = null;

		for (Cooperation cooperation : cooperations) {
			if (max == null)
				max = cooperation;

			if (cooperation.getDurationCooperationInDays() > max.getDurationCooperationInDays())
				max = cooperation;
		}
		return max;
	}

	// saves all the matches that occur between colleagues in common projects in a
	// separate class
	private static List<Cooperation> saveCooperationInformation(List<Employee> employees) {

		long cooperationInDays = 0;
		List<Cooperation> cooperations = new ArrayList<>();

		for (int i = 0; i < employees.size(); i++) {

			for (int j = i; j < employees.size(); j++) {

				if (employees.get(i).getEmployeeId() == employees.get(j).getEmployeeId())
					continue;

				if (employees.get(i).getProjectId() != employees.get(j).getProjectId())
					continue;

				cooperationInDays = calculateOverlap(employees.get(i).getDateFrom(), employees.get(i).getDateTo(),
						employees.get(j).getDateFrom(), employees.get(j).getDateTo());

				if (cooperationInDays == 0)
					continue;

				Cooperation existingMatch = findMatch(cooperations, employees.get(i).getEmployeeId(),
						employees.get(j).getEmployeeId());

				if (existingMatch == null)
					cooperations.add(new Cooperation(cooperationInDays, employees.get(i).getEmployeeId(),
							employees.get(j).getEmployeeId()));

				else {
					cooperationInDays += existingMatch.getDurationCooperationInDays();
					existingMatch.setDurationCooperationInDays(cooperationInDays);
				}
			}
		}
		return cooperations;
	}

	// checks the current couple of employees: if they have already worked together
	// on a project or if it's their first time
	private static Cooperation findMatch(List<Cooperation> coops, Integer emplId1, Integer emplId2) {
		for (Cooperation coop : coops) {
			if ((coop.getEmployeeId1() == emplId1 || coop.getEmployeeId2() == emplId1)
					&& (coop.getEmployeeId1() == emplId2 || coop.getEmployeeId2() == emplId2))
				return coop;
		}
		return null;
	}

	private static Employee createEmployeeRecord(String[] metadata) {
		final int employeeId = Integer.parseInt(metadata[0].trim());
		final int projectId = Integer.parseInt(metadata[1].trim());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date dateFrom = new Date();
		Date dateTo = new Date();
		try {
			String dateFromRaw = metadata[2].trim();
			if (!dateFromRaw.equals(NULL))
				dateFrom = formatter.parse(dateFromRaw);

		} catch (ParseException e) {

			e.printStackTrace();
		}
		try {
			String dateToRaw = metadata[3].trim();
			if (!dateToRaw.equals(NULL))
				dateTo = formatter.parse(dateToRaw);
		} catch (ParseException e) {

			e.printStackTrace();
		}
		return new Employee(employeeId, projectId, dateFrom, dateTo);
	}

	private static long calculateOverlap(Date startDate1, Date endDate1, Date startDate2, Date endDate2) {

		if (startDate1.after(endDate2) || endDate1.before(startDate2))
			return 0;

		final Date startOverlap = startDate1.after(startDate2) ? startDate1 : startDate2;
		final Date endOverlap = endDate1.before(endDate2) ? endDate1 : endDate2;

		final long diffInMillies = Math.abs(endOverlap.getTime() - startOverlap.getTime());
		return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

	}

}

class Cooperation {
	private Long durationCooperationInDays;
	private Integer employeeId1;
	private Integer employeeId2;

	public Cooperation(Long durationCooperationInDays, Integer employeeId1, Integer employeeId2) {
		this.durationCooperationInDays = durationCooperationInDays;
		this.employeeId1 = employeeId1;
		this.employeeId2 = employeeId2;
	}

	public Long getDurationCooperationInDays() {
		return durationCooperationInDays;
	}

	public void setDurationCooperationInDays(Long durationCooperationInDays) {
		this.durationCooperationInDays = durationCooperationInDays;
	}

	public Integer getEmployeeId1() {
		return employeeId1;
	}

	public void setEmployeeId(Integer employeeId1) {
		this.employeeId1 = employeeId1;
	}

	public Integer getEmployeeId2() {
		return employeeId2;
	}

	public void setEmployeeId2(Integer employeeId2) {
		this.employeeId2 = employeeId2;
	}
}

class Employee {
	private Integer employeeId;
	private Integer projectId;
	private Date dateFrom;
	private Date dateTo;

	public Employee(Integer employeeId, Integer projectId, Date dateFrom, Date dateTo) {
		this.employeeId = employeeId;
		this.projectId = projectId;
		this.dateTo = dateTo;
		this.dateFrom = dateFrom;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}
}
