package dao;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import entities.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserDAO {

	private final EntityManager em;

	public UserDAO(EntityManager em) {
		this.em = em;
	}

	public List<User> getAllUser() {
		try {
			TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class);
			List<User> userList = query.getResultList();
			StringBuilder users = new StringBuilder("Lista Utenti:\n");
			for (User user : userList) {
				users.append(user.toString()).append("\n");
			}
			System.out.println(users.toString());
			return userList;
		} catch (Exception e) {
			log.error("error: " + e);
			throw e;
		}

	};

	public void saveUser(User a) {

		EntityTransaction t = em.getTransaction();
		try {
			t.begin();
			em.persist(a);
			t.commit();
			log.info("Saved user!!");

		} catch (Exception e) {
			log.error("errore: " + e);
		}
	}

	public void deleteUser(UUID id) {

		EntityTransaction t = em.getTransaction();
		try {
			t.begin();
			User user = em.find(User.class, id);
			em.remove(user);
			t.commit();
			log.info("User delete");
		} catch (Exception e) {
			log.error("errore: " + e);
		}
	};

	public User findById(String id) {
		User found = em.find(User.class, UUID.fromString(id));
		return found;
	}

}
