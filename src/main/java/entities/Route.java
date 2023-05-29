package entities;

import java.time.Duration;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "routes")
@Getter
@Setter
@NoArgsConstructor
public class Route {

	@Id
	@GeneratedValue
	private UUID id;

	private String startPoint;
	private String terminus;

	private int avgTime;

	private int numberOfTimes;
	private Duration travelTime;

	@OneToOne
	private Vehicle vehicle;

	public Route(String startPoint, String terminus, int avgTime, int numberOfTimes, Duration travelTime) {
		super();
		this.startPoint = startPoint;
		this.terminus = terminus;
		this.avgTime = avgTime;
		this.numberOfTimes = numberOfTimes;
		this.travelTime = travelTime;
	}

	@Override
	public String toString() {
		return "Route{" + "id=" + id + ", startPoint='" + startPoint + '\'' + ", terminus='" + terminus + '\''
				+ ", avgTime=" + avgTime + ", numberOfTimes=" + numberOfTimes + ", travelTime=" + travelTime + '}';
	}

}
