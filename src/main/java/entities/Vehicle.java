package entities;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class Vehicle {
	public enum State {
		IN_SERVICE, UNDER_MAINTENANCE
	}

	public enum Type {
		BUS, TRAM
	}

	@Id
	@GeneratedValue
	private UUID id;

	@Enumerated(EnumType.STRING)
	private State state;

	@Enumerated(EnumType.STRING)
	private Type type;

	private int capacity;

	private int ticketsValidated;

	private LocalDate serviceStartDate;
	private LocalDate maintenanceStartDate;

	private int maintenanceCount;
	private Duration totalMaintenanceDuration;

	private int serviceCount;
	private Duration totalServiceDuration;

	@OneToOne(mappedBy = "vehicle")
	private Route route;

	@OneToMany(mappedBy = "vehicle")
	private List<Ticket> ticketsList = new ArrayList<>();

	public Vehicle(Integer cap, Type type) {
		this.state = State.IN_SERVICE;
		this.ticketsValidated = 0;
		this.serviceStartDate = LocalDate.now();
		this.maintenanceStartDate = null;
		this.maintenanceCount = 0;
		this.totalMaintenanceDuration = Duration.ZERO;
		this.serviceCount = 0;
		this.totalServiceDuration = Duration.ZERO;
		this.capacity = cap;
		this.type = type;
	}

	@Override
	public String toString() {
		return "Vehicle{" + "id=" + id + ", state=" + state + ", type=" + type + ", capacity=" + capacity
				+ ", ticketsValidated=" + ticketsValidated + ", serviceStartDate=" + serviceStartDate
				+ ", maintenanceStartDate=" + maintenanceStartDate + ", maintenanceCount=" + maintenanceCount
				+ ", totalMaintenanceDuration=" + totalMaintenanceDuration + ", serviceCount=" + serviceCount
				+ ", totalServiceDuration=" + totalServiceDuration + '}';
	}

}
