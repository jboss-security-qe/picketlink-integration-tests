# PicketLink Federation: Integration Tests #

Refactored version of PicketLink Integration Tests. It has some new settings and abilities, see below for their description. For some informations about PicketLink and original PicketLink Integration Tests see https://docs.jboss.org/author/display/PLINK/Integration+Tests.

## Before testing ##

You need to install PicketLink Quickstarts to your maven repository, follow instructions in https://docs.jboss.org/author/display/PLINK/PicketLink+Quickstarts.
In some cases you need to install PicketLink Federation to your maven repository, follow instructions in https://github.com/picketlink2/federation.

## Running testsuite ##

You can running testsuite by command:
	mvn clean install

However to run some tests you need call some of tests profile, see below for profiles descriptions. Also there are some tests properties describes in Properties section below.

If you want to use your own container, you need to copy it to picketlink-integration-tests/integration-tests/dist/ folder. If you use EAP 6 it can be downloaded to dist folder by testsuite (see Miscellaneous section below for EAP 6.0 specifics).

## Containers ##

PicketLink Integration Testsuite has these containers: eap-5, eap-6, jboss-as5, jboss-as6, jboss-as7, tomcat-6.

## Profiles ##

Containers profiles
	- community - default profile, run jboss-as7, jboss-as5, jboss-as6 and tomcat-6 containers.
	- eap5 - run eap-5 container
	- eap6 - run eap-6 container
	- jbas7 - run jboss-as7 container

Tests profiles
	- all-tests
	- saml-tests 
	- ws-trust-tests 
	- xacml-tests 
	- trust-tests

Copying PicketLink profiles (it is used in ... containers)
	- copy-picketlink-from-maven - default, it copies PicketLink Federation from your maven repository. You need to install PicketLink Federation to your maven repository before running integration testsuite
	- copy-picketlink-from-lib - it copies PicketLink Federation from picketlink.dist.dir and it work only if property picketlink.dist.dir is set!
	- skip-copy-picketlink - use PicketLink distributes in container

Example
It runs testsuit in eap6 profile, runs all tests and copies PicketLink from maven repository:
	mvn clean install -Peap6,all-tests,copy-picketlink-from-maven 

## Tests ##

Tests are divided into packages according to containers. We have 5 types of packages: jbas7, eap6, jbas5, eap5 and common. Every profile runs test in some package according to:
	- profile jbas7 - runs jbas7 and some includes from commons
       	- eap6 - eap6, jbas7 and some includes from commons
       	- jbas5 - jbas5, commons
       	- eap5 - eap5, jbas5, commons
	- jbas6 - commons and some excludes
	- tomcat6 - commons and some excludes
(jbas5, jbas6 and tomcat6 aren't actually profiles in maven meaning, their are only modules of community profile)
Which tests are actually running is defined in maven-failsafe-plugin in integration-tests/CONTAINER/pom.xml.
If you write a new test please add its to related package.

## Properties ##

- eap6-dist-zip - name of EAP6 distribution located in picketlink-integration-tests/integration-tests/dist/ !!!property will be rewritten for using in every container!!!
- eap6-dir-structure - name of folder with eap6 used in zip distribution, default is jboss-eap-6.1
- version.picketlink - sets version of PicketLink which you want to use, current default is 2.1.8-SNAPSHOT
- version.picketlink.quickstarts - sets version of PicketLink Quickstarts which you want to use, it use same version as version.picketlink by default, but you can change it

Examples
It runs testsuit in eap6 profile, runs all tests and copies PicketLink 2.1.6.Final from maven repository:
	mvn clean install -Peap6,all-tests,copy-picketlink-from-maven -Dversion.picketlink=2.1.6.Final

It runs testsuit in eap6 profile, runs all tests, uses PicketLink from from container, runs with jboss-eap-6.0.1 (you also need to change eap6-dir-structure):
	mvn clean install -Peap6,all-tests,copy-picketlink-from-maven -Deap6-dist-zip=jboss-eap-6.0.1.zip -Deap6-dir-structure=jboss-eap-6.0

## Miscellaneous ##

Settings for EAP 6.0 
	- eap6-dist-url - if you want download EAP 6 by testsuite, you need to specified url (for example http://download.devel.redhat.com/released/JBEAP-6/6.0.0/zip/jboss-eap-6.0.0.zip)
	- jboss.as.modules.structure - set it to 'modules' (-Djboss.as.modules.structure=modules)
	- version.remoting3 - if you use eap-6.0.0 set remoting to 3.2.8.GA (don't do it fo eap-6.0.1!)


