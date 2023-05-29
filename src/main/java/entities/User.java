package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
public class User {
	@Id
	@GeneratedValue
	protected UUID Id;
	protected String name;
	protected String lastName;

	@OneToOne(mappedBy = "user")
	private Card card;

	@OneToMany(mappedBy = "user")
	private List<Ticket> tickets = new ArrayList<>();

	public User(String name, String lastName) {
		this.name = name;
		this.lastName = lastName;
	};

	@Override
	public String toString() {
		return "Utente = " + Id + " nome: " + name + " cognome: " + lastName;
	};
}
