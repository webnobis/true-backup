/**
 * True backup module
 *
 * @author Steffen Nobis
 */
module com.webnobis.truebackup {

    requires org.slf4j;
    requires org.apache.commons.cli;

    exports com.webnobis.truebackup;
    exports com.webnobis.truebackup.progress;

}