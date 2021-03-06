/**
 * This file is part of D.A.L.G.S.
 *
 * D.A.L.G.S is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * D.A.L.G.S is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with D.A.L.G.S.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.ucm.fdi.dalgs.course.repository;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import es.ucm.fdi.dalgs.domain.AcademicTerm;
import es.ucm.fdi.dalgs.domain.Course;
import es.ucm.fdi.dalgs.domain.Subject;
import es.ucm.fdi.dalgs.domain.User;

@Repository
public class CourseRepository {
	protected EntityManager em;
	protected static final Logger logger = LoggerFactory
			.getLogger(CourseRepository.class);

	public EntityManager getEntityManager() {
		return em;
	}

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		try {
			this.em = entityManager;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public boolean addCourse(Course course) {
		try {
			em.persist(course);
		} catch (ConstraintViolationException e) {
			logger.error(e.getMessage());
			return false;
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	public List<Course> getAll() {
		return em
				.createQuery(
						"select d from Course d where d.isDeleted='false' order by d.id")
				.getResultList();
	}

	public boolean saveCourse(Course course) {
		try {
			em.merge(course);
		} catch (ConstraintViolationException e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}

	public Course getCourse(Long id_course, Long id_academic) {
		AcademicTerm academic = em
				.getReference(AcademicTerm.class, id_academic);
		Query query = em
				.createQuery("select c from Course c where c.academicTerm=?1 and c.id=?2");
		query.setParameter(1, academic);
		query.setParameter(2, id_course);

		if (query.getResultList().isEmpty())
			return null;
		else
			return (Course) query.getSingleResult();
	}

	public Course getCourseFormatter(Long id) {
		return em.find(Course.class, id);
	}

	public boolean deleteCourse(Course course) {
		try {
			course.setDeleted(true);
			em.merge(course);

			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
	}

	public Course exist(Course course) {
		Query query = em
				.createQuery("select c from Course c  where c.academicTerm=?1 and c.subject=?2");
		query.setParameter(1, course.getAcademicTerm());
		query.setParameter(2, course.getSubject());

		if (query.getResultList().isEmpty())
			return null;
		else
			return (Course) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public List<Course> getCoursesByAcademicTerm(Long academic_id,
			Boolean showAll) {
		AcademicTerm academic = em
				.getReference(AcademicTerm.class, academic_id);

		Query query = null;

		if (showAll)
			query = em
					.createQuery("select c from Course c  join c.academicTerm a  where a=?1 ");
		else
			query = em
					.createQuery("select c from Course c  join c.academicTerm a  where a=?1 and c.isDeleted='false'");

		query.setParameter(1, academic);

		return query.getResultList();

	}

	public Long isDisabled(Long id_academic, Long id_subject) {
		Subject subject = em.getReference(Subject.class, id_subject);
		AcademicTerm academic = em
				.getReference(AcademicTerm.class, id_academic);

		Query query = em
				.createQuery("select c from Course c where c.subject=?1 and c.academicTerm=?2 and c.isDeleted=1");
		query.setParameter(1, subject);
		query.setParameter(2, academic);

		if (query.getResultList().isEmpty())
			return null;

		Course aux = (Course) query.getSingleResult();
		return aux.getId();
	}

	public boolean deleteCoursesFromAcademic(AcademicTerm academic) {
		try {
			Query query = em
					.createQuery("UPDATE Course c SET c.isDeleted = true where c.academicTerm=?1");

			query.setParameter(1, academic);
			query.executeUpdate();

		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;

	}

	@SuppressWarnings("unchecked")
	public Collection<Course> getCoursesFromListAcademic(
			Collection<AcademicTerm> academicList) {
		Query query = em
				.createQuery("SELECT c  FROM Course c WHERE c.isDeleted = false  AND c.academicTerm in ?1");
		query.setParameter(1, academicList);

		if (query.getResultList().isEmpty())
			return null;
		else
			return (Collection<Course>) query.getResultList();
	}

	public boolean deleteCourses(Collection<AcademicTerm> academicList) {

		try {
			Query query = em
					.createQuery("UPDATE Course c SET c.isDeleted = true WHERE c.academicTerm in ?1");
			query.setParameter(1, academicList);
			int n = query.executeUpdate();
			if (n > 0)
				return true;
			else
				return false;

		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}

	}

	@SuppressWarnings("unchecked")
	public Collection<Course> getCoursesBySubject(Subject subject) {

		Query query = em
				.createQuery("select c from Course c  join c.subject s  where s=?1 and c.isDeleted='false'");

		query.setParameter(1, subject);

		return query.getResultList();
	}

	public Boolean deleteCoursesForSubject(Collection<Subject> subjects) {

		try {
			Query query = em
					.createQuery("UPDATE Course c SET c.isDeleted = true WHERE c.subject in ?1");
			query.setParameter(1, subjects);
			int n = query.executeUpdate();
			if (n > 0)
				return true;
			else
				return false;

		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public Collection<Course> getCoursesByUser(Long id_user) {
		User user = em.getReference(User.class, id_user);

		Query query = em
				.createQuery("select c from Course c   where c.coordinator=?1 and c.isDeleted='false'");
		query.setParameter(1, user);
		return query.getResultList();
	}

}
