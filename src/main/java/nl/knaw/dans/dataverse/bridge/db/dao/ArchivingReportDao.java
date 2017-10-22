package nl.knaw.dans.dataverse.bridge.db.dao;

import nl.knaw.dans.dataverse.bridge.db.domain.ArchivingReport;
import nl.knaw.dans.dataverse.bridge.db.domain.DvnTdrUser;
import nl.knaw.dans.dataverse.bridge.util.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Class ArchivingReportDao
 * Created by Eko Indarto
 * <p>
 * This class is used to access data for the ArchivingReport entity.
 * Tdr annotation allows the component scanning support to find and
 * configure the DAO wihtout any XML configuration and also provide the Spring
 * exceptiom translation.
 * Since we've setup setPackagesToScan and transaction manager on
 * DatabaseConfig, any bean method annotated with Transactional will cause
 * Spring to magically call begin() and commit() at the start/end of the
 * method. If exception occurs it will also call rollback().
 */

@Repository
@Transactional
public class ArchivingReportDao {
    private static final Logger LOG = LoggerFactory.getLogger(ArchivingReportDao.class);
    // An EntityManager will be automatically injected from entityManagerFactory
    // setup on DatabaseConfig class.
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Save the archivingReport in the database.
     */
    public void create(ArchivingReport archivingReport) {
        entityManager.persist(archivingReport);
        return;
    }

    /**
     * Delete the archivingReport from the database.
     */
    public void delete(ArchivingReport archivingReport) {
        if (entityManager.contains(archivingReport))
            entityManager.remove(archivingReport);
        else
            entityManager.remove(entityManager.merge(archivingReport));
        return;
    }

    /**
     * Return all the archivingReports stored in the database.
     */
    @SuppressWarnings("unchecked")
    public List<ArchivingReport> getAll() {
        return entityManager.createQuery("from ArchivingReport").getResultList();
    }


    /**
     * Update the passed archivingReport in the database.
     */
    public void update(ArchivingReport archivingReport) {
        entityManager.merge(archivingReport);
        return;
    }

    public ArchivingReport getById(long id) {
        return entityManager.find(ArchivingReport.class, id);
    }

    public ArchivingReport findByDatasetAndVersionAndDvnTdrUserId(String dataset, int version, DvnTdrUser dvnTdrUser) {
        Query query = entityManager.createQuery(
                "from ArchivingReport where dataset = :dataset and version = :version and dvnTdrUser = :dvnTdrUser")
                .setParameter("dataset", dataset)
                .setParameter("version", version)
                .setParameter("dvnTdrUser", dvnTdrUser);
        try {
            return (ArchivingReport) query.getSingleResult();
        } catch (NoResultException nre) {
            //Ignore this because as per our logic this is ok!
        }
        LOG.info("No reporting data found where dataset: " + dataset + " version: " + version + " and dataverse user : " + dvnTdrUser.getDvnUser());
        return null;
    }

    public ArchivingReport findByDatasetAndStatusAndDvnTdrUserId(String dataset, Status status, DvnTdrUser dvnTdrUser) {
        Query query = entityManager.createQuery(
                "from ArchivingReport where dataset = :dataset and status = :status and dvnTdrUser = :dvnTdrUser")
                .setParameter("dataset", dataset)
                .setParameter("status", status.toString())
                .setParameter("dvnTdrUser", dvnTdrUser);
        try {
            return (ArchivingReport) query.getSingleResult();
        } catch (NoResultException nre) {
            //Ignore this because as per our logic this is ok!
        }
        LOG.info("No reporting data found where dataset: " + dataset + " status: " + status.toString() + " and dataverse user : " + dvnTdrUser.getDvnUser());
        return null;
    }

    public List<ArchivingReport> getAllIngestedDatasetsByStatus(Status status) {
        Query query = entityManager.createQuery(
                "from ArchivingReport where status = :status")
                .setParameter("status", status.toString());
        try {
            return (List<ArchivingReport>) query.getResultList();
        } catch (NoResultException nre) {
            //Ignore this because as per our logic this is ok!
        }
        LOG.info("No reporting data found with status " + status);
        return null;
    }

    public ArchivingReport  findByDatasetDvnTdrUserIds(String dataset, List<DvnTdrUser> dvnTdrUsers) {
        Query query = entityManager.createQuery(
                "from ArchivingReport where dataset = :dataset and dvnTdrUser in :dvnTdrUsers")
                .setParameter("dataset", dataset)
                .setParameter("dvnTdrUsers", dvnTdrUsers);
        try {
            return (ArchivingReport) query.getSingleResult();
        } catch (NoResultException nre) {
            //Ignore this because as per our logic this is ok!
        }
        LOG.info("No reporting data found for dataset: " + dataset);
        return null;
    }
}

