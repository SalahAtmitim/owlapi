package org.semanticweb.owlapitools.profiles.test;

import static org.junit.Assert.*;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.IRI;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.util.OWLObjectPropertyManager;
import org.semanticweb.owlapitools.profiles.OWL2DLProfile;
import org.semanticweb.owlapitools.profiles.OWL2RLProfile;
import org.semanticweb.owlapitools.profiles.OWLProfileReport;
import org.semanticweb.owlapitools.profiles.OWLProfileViolation;
import org.semanticweb.owlapitools.profiles.violations.UseOfReservedVocabularyForAnnotationPropertyIRI;
import org.semanticweb.owlapitools.profiles.violations.UseOfUndeclaredAnnotationProperty;

@SuppressWarnings("javadoc")
public class ForbiddenVocabularyTestCase {
    @Test
    public void shouldFindViolation() throws Exception {
        String input = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" ><owl:Ontology rdf:about=\"\"/>\n<owl:Class rdf:about=\"http://phenomebrowser.net/cellphenotype.owl#C3PO:000000015\"><rdf:Description rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">Any.</rdf:Description></owl:Class></rdf:RDF>";
        OWLOntology o = OWLManager.createOWLOntologyManager()
                .loadOntologyFromOntologyDocument(
                        new StringDocumentSource(input));
        OWL2DLProfile p = new OWL2DLProfile();
        OWLProfileReport checkOntology = p.checkOntology(o);
        assertEquals(2, checkOntology.getViolations().size());
        OWLProfileViolation<?> v = checkOntology.getViolations().get(0);
        assertTrue(v instanceof UseOfUndeclaredAnnotationProperty
                || v instanceof UseOfReservedVocabularyForAnnotationPropertyIRI);
        v = checkOntology.getViolations().get(1);
        assertTrue(v instanceof UseOfUndeclaredAnnotationProperty
                || v instanceof UseOfReservedVocabularyForAnnotationPropertyIRI);
    }

    @Test
    public void testGenIdGalenFragment() throws OWLOntologyCreationException {
        String test = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF \n"
                + "     xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
                + "     xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "     xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
                + "     xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + "    <owl:Ontology rdf:about=\"http://www.co-ode.org/ontologies/galen\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.co-ode.org/ontologies/galen#hasQuantity\"/>\n"
                + "<owl:Class rdf:about=\"http://www.co-ode.org/ontologies/galen#test\">\n"
                + "<rdfs:subClassOf><owl:Restriction>\n"
                + "<owl:onProperty rdf:resource=\"http://www.co-ode.org/ontologies/galen#hasQuantity\"/>\n"
                + "<owl:someValuesFrom><owl:Class rdf:about=\"http://www.co-ode.org/ontologies/galen#anotherTest\"/></owl:someValuesFrom>\n"
                + "</owl:Restriction></rdfs:subClassOf></owl:Class></rdf:RDF>";
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m
                .loadOntologyFromOntologyDocument(new StringDocumentSource(test));
        OWL2DLProfile profile = new OWL2DLProfile();
        OWLProfileReport report = profile.checkOntology(o);
        assertTrue(report.isInProfile());
    }

    @Test
    public void testOWLEL() throws OWLOntologyCreationException {
        String onto = "<?xml version=\"1.0\"?>\n"
                + "<!DOCTYPE rdf:RDF [\n"
                + "<!ENTITY owl \"http://www.w3.org/2002/07/owl#\" >\n"
                + "<!ENTITY rdfs \"http://www.w3.org/2000/01/rdf-schema#\" >\n"
                + "<!ENTITY rdf \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" >\n"
                + "]>\n"
                + "<rdf:RDF xmlns=\"http://xmlns.com/foaf/0.1/\"\n"
                + "xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + "xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
                + "xmlns:owl=\"http://www.w3.org/2002/07/owl#\">\n"
                + "<owl:Ontology rdf:about=\"http://ex.com\"/>\n"
                + "<rdf:Property rdf:about=\"http://ex.com#p1\">\n"
                + "<rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#DatatypeProperty\"/>\n"
                + "</rdf:Property>\n"
                + "<rdf:Property rdf:about=\"http://ex.com#p2\">\n"
                + "<rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#DatatypeProperty\"/>\n"
                + "<rdfs:subPropertyOf rdf:resource=\"http://ex.com#p1\"/>\n"
                + "</rdf:Property>\n" + "</rdf:RDF>";
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m
                .loadOntologyFromOntologyDocument(new StringDocumentSource(onto));
        OWL2RLProfile p = new OWL2RLProfile();
        OWLProfileReport report = p.checkOntology(o);
        assertTrue("unexpected violations! " + report.getViolations(), report
                .getViolations().isEmpty());
    }

    @Test
    public void shouldCauseViolationsWithUseOfPropertyInChain()
            throws OWLOntologyCreationException {
        OWLOntology o = OWLManager.createOWLOntologyManager().createOntology();
        OWLDataFactory f = OWLManager.getOWLDataFactory();
        // SubObjectPropertyOf( ObjectPropertyChain( a:hasFather a:hasBrother )
        // a:hasUncle ) The brother of someone's father is that person's uncle.
        // SubObjectPropertyOf( ObjectPropertyChain( a:hasChild a:hasUncle )
        // a:hasBrother ) The uncle of someone's child is that person's brother.
        OWLObjectProperty father = f
                .getOWLObjectProperty(IRI("urn:test:hasFather"));
        OWLObjectProperty brother = f
                .getOWLObjectProperty(IRI("urn:test:hasBrother"));
        OWLObjectProperty child = f
                .getOWLObjectProperty(IRI("urn:test:hasChild"));
        OWLObjectProperty uncle = f
                .getOWLObjectProperty(IRI("urn:test:hasUncle"));
        o.getOWLOntologyManager().addAxiom(o, f.getOWLDeclarationAxiom(father));
        o.getOWLOntologyManager()
                .addAxiom(o, f.getOWLDeclarationAxiom(brother));
        o.getOWLOntologyManager().addAxiom(o, f.getOWLDeclarationAxiom(child));
        o.getOWLOntologyManager().addAxiom(o, f.getOWLDeclarationAxiom(uncle));
        OWLSubPropertyChainOfAxiom brokenAxiom1 = f
                .getOWLSubPropertyChainOfAxiom(Arrays.asList(father, brother),
                        uncle);
        OWLSubPropertyChainOfAxiom brokenAxiom2 = f
                .getOWLSubPropertyChainOfAxiom(Arrays.asList(child, uncle),
                        brother);
        OWLObjectPropertyManager manager = new OWLObjectPropertyManager(
                o.getOWLOntologyManager(), o);
        o.getOWLOntologyManager().addAxiom(o, brokenAxiom1);
        o.getOWLOntologyManager().addAxiom(o, brokenAxiom2);
        assertTrue(manager.isLessThan(brother, uncle));
        assertTrue(manager.isLessThan(uncle, brother));
        assertTrue(manager.isLessThan(brother, brother));
        assertTrue(manager.isLessThan(uncle, uncle));
        OWL2DLProfile profile = new OWL2DLProfile();
        List<OWLProfileViolation<?>> violations = profile.checkOntology(o)
                .getViolations();
        assertFalse(violations.isEmpty());
        for (OWLProfileViolation<?> v : violations) {
            assertTrue(v.getAxiom().equals(brokenAxiom1)
                    || v.getAxiom().equals(brokenAxiom2));
        }
    }

    @Test
    public void shouldNotCauseViolations() throws OWLOntologyCreationException {
        OWLOntology o = OWLManager.createOWLOntologyManager().createOntology();
        OWLDataFactory f = OWLManager.getOWLDataFactory();
        OWLObjectProperty father = f
                .getOWLObjectProperty(IRI("urn:test:hasFather"));
        OWLObjectProperty brother = f
                .getOWLObjectProperty(IRI("urn:test:hasBrother"));
        OWLObjectProperty child = f
                .getOWLObjectProperty(IRI("urn:test:hasChild"));
        OWLObjectProperty uncle = f
                .getOWLObjectProperty(IRI("urn:test:hasUncle"));
        o.getOWLOntologyManager().addAxiom(o, f.getOWLDeclarationAxiom(father));
        o.getOWLOntologyManager()
                .addAxiom(o, f.getOWLDeclarationAxiom(brother));
        o.getOWLOntologyManager().addAxiom(o, f.getOWLDeclarationAxiom(child));
        o.getOWLOntologyManager().addAxiom(o, f.getOWLDeclarationAxiom(uncle));
        OWLSubPropertyChainOfAxiom brokenAxiom1 = f
                .getOWLSubPropertyChainOfAxiom(Arrays.asList(father, brother),
                        uncle);
        OWLObjectPropertyManager manager = new OWLObjectPropertyManager(
                o.getOWLOntologyManager(), o);
        o.getOWLOntologyManager().addAxiom(o, brokenAxiom1);
        assertTrue(manager.isLessThan(brother, uncle));
        OWL2DLProfile profile = new OWL2DLProfile();
        List<OWLProfileViolation<?>> violations = profile.checkOntology(o)
                .getViolations();
        assertTrue(violations.isEmpty());
        for (OWLProfileViolation<?> v : violations) {
            assertTrue(v.getAxiom().equals(brokenAxiom1));
        }
    }

    String input1 = "<?xml version=\"1.0\"?>\n"
            + "<rdf:RDF xmlns=\"http://purl.org/net/social-reality#\"\n"
            + "     xml:base=\"http://purl.org/net/social-reality\"\n"
            + "     xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
            + "     xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
            + "     xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
            + "     xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
            + "    <owl:Ontology rdf:about=\"http://purl.org/net/social-reality\"/>\n"
            + "    <owl:ObjectProperty rdf:about=\"http://purl.org/net/social-reality#context\">\n"
            + "        <rdfs:subPropertyOf rdf:resource=\"http://www.w3.org/2002/07/owl#topObjectProperty\"/>\n"
            + "    </owl:ObjectProperty>\n"
            + "    <owl:ObjectProperty rdf:about=\"http://purl.org/net/social-reality#counts-as\">\n"
            + "        <rdfs:subPropertyOf rdf:resource=\"http://www.w3.org/2002/07/owl#topObjectProperty\"/>\n"
            + "    </owl:ObjectProperty>\n"
            + "    <owl:ObjectProperty rdf:about=\"http://purl.org/net/social-reality#has_OR\">\n"
            + "        <rdfs:subPropertyOf rdf:resource=\"http://www.w3.org/2002/07/owl#topObjectProperty\"/>\n"
            + "        <owl:propertyChainAxiom rdf:parseType=\"Collection\">\n"
            + "            <rdf:Description>\n"
            + "                <owl:inverseOf rdf:resource=\"http://purl.org/net/social-reality#context\"/>\n"
            + "            </rdf:Description>\n"
            + "            <rdf:Description rdf:about=\"http://purl.org/net/social-reality#is_OR\"/>\n"
            + "            <rdf:Description>\n"
            + "                <owl:inverseOf rdf:resource=\"http://purl.org/net/social-reality#counts-as\"/>\n"
            + "            </rdf:Description>\n"
            + "        </owl:propertyChainAxiom>\n"
            + "    </owl:ObjectProperty>\n"
            + "    <owl:ObjectProperty rdf:about=\"http://purl.org/net/social-reality#is_OR\">\n"
            + "        <rdfs:subPropertyOf rdf:resource=\"http://www.w3.org/2002/07/owl#topObjectProperty\"/>\n"
            + "    </owl:ObjectProperty>\n"
            + "    <owl:ObjectProperty rdf:about=\"http://www.w3.org/2002/07/owl#topObjectProperty\"/>\n"
            + "    <owl:Class rdf:about=\"http://purl.org/net/social-reality#BF\"/>\n"
            + "    <owl:Class rdf:about=\"http://purl.org/net/social-reality#C\"/>\n"
            + "    <owl:Class rdf:about=\"http://purl.org/net/social-reality#OR\">\n"
            + "        <owl:equivalentClass>\n"
            + "            <owl:Class>\n"
            + "                <owl:intersectionOf rdf:parseType=\"Collection\">\n"
            + "                    <owl:Restriction>\n"
            + "                        <owl:onProperty rdf:resource=\"http://purl.org/net/social-reality#context\"/>\n"
            + "                        <owl:someValuesFrom rdf:resource=\"http://purl.org/net/social-reality#C\"/>\n"
            + "                    </owl:Restriction>\n"
            + "                    <owl:Restriction>\n"
            + "                        <owl:onProperty>\n"
            + "                            <rdf:Description>\n"
            + "                                <owl:inverseOf rdf:resource=\"http://purl.org/net/social-reality#counts-as\"/>\n"
            + "                            </rdf:Description>\n"
            + "                        </owl:onProperty>\n"
            + "                        <owl:someValuesFrom rdf:resource=\"http://purl.org/net/social-reality#BF\"/>\n"
            + "                    </owl:Restriction>\n"
            + "                </owl:intersectionOf>\n"
            + "            </owl:Class>\n"
            + "        </owl:equivalentClass>\n"
            + "        <owl:equivalentClass>\n"
            + "            <owl:Restriction>\n"
            + "                <owl:onProperty rdf:resource=\"http://purl.org/net/social-reality#is_OR\"/>\n"
            + "                <owl:hasSelf rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">true</owl:hasSelf>\n"
            + "            </owl:Restriction>\n"
            + "        </owl:equivalentClass>\n"
            + "        <rdfs:subClassOf>\n"
            + "            <owl:Class>\n"
            + "                <owl:intersectionOf rdf:parseType=\"Collection\">\n"
            + "                    <owl:Restriction>\n"
            + "                        <owl:onProperty rdf:resource=\"http://purl.org/net/social-reality#context\"/>\n"
            + "                        <owl:cardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:cardinality>\n"
            + "                    </owl:Restriction>\n"
            + "                    <owl:Restriction>\n"
            + "                        <owl:onProperty>\n"
            + "                            <rdf:Description>\n"
            + "                                <owl:inverseOf rdf:resource=\"http://purl.org/net/social-reality#counts-as\"/>\n"
            + "                            </rdf:Description>\n"
            + "                        </owl:onProperty>\n"
            + "                        <owl:cardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:cardinality>\n"
            + "                    </owl:Restriction>\n"
            + "                </owl:intersectionOf>\n"
            + "            </owl:Class>\n"
            + "        </rdfs:subClassOf>\n"
            + "    </owl:Class>\n"
            + "    <owl:Class rdf:about=\"http://www.w3.org/2002/07/owl#Thing\"/>\n"
            + "</rdf:RDF>";
    String input2 = "<?xml version=\"1.0\"?>\n"
            + "<rdf:RDF xmlns=\"http://purl.org/net/roles#\"\n"
            + "     xml:base=\"http://purl.org/net/roles\"\n"
            + "     xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
            + "     xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
            + "     xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
            + "     xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
            + "    <owl:Ontology rdf:about=\"http://purl.org/net/roles\"/>\n"
            + "    <owl:ObjectProperty rdf:about=\"http://purl.org/net/roles#has_F\">\n"
            + "        <rdfs:subPropertyOf rdf:resource=\"http://www.w3.org/2002/07/owl#topObjectProperty\"/>\n"
            + "        <owl:propertyChainAxiom rdf:parseType=\"Collection\">\n"
            + "            <rdf:Description rdf:about=\"http://purl.org/net/roles#is_Ac\"/>\n"
            + "            <rdf:Description>\n"
            + "                <owl:inverseOf rdf:resource=\"http://purl.org/net/social-reality#context\"/>\n"
            + "            </rdf:Description>\n"
            + "            <rdf:Description rdf:about=\"http://purl.org/net/roles#is_F\"/>\n"
            + "            <rdf:Description>\n"
            + "                <owl:inverseOf rdf:resource=\"http://purl.org/net/roles#plays\"/>\n"
            + "            </rdf:Description>\n"
            + "            <rdf:Description rdf:about=\"http://purl.org/net/roles#is_Ar\"/>\n"
            + "        </owl:propertyChainAxiom>\n"
            + "    </owl:ObjectProperty>\n"
            + "    <owl:ObjectProperty rdf:about=\"http://purl.org/net/roles#has_R\">\n"
            + "        <rdfs:subPropertyOf rdf:resource=\"http://www.w3.org/2002/07/owl#topObjectProperty\"/>\n"
            + "        <owl:propertyChainAxiom rdf:parseType=\"Collection\">\n"
            + "            <rdf:Description>\n"
            + "                <owl:inverseOf rdf:resource=\"http://purl.org/net/social-reality#context\"/>\n"
            + "            </rdf:Description>\n"
            + "            <rdf:Description rdf:about=\"http://purl.org/net/roles#is_R\"/>\n"
            + "            <rdf:Description>\n"
            + "                <owl:inverseOf rdf:resource=\"http://purl.org/net/roles#plays\"/>\n"
            + "            </rdf:Description>\n"
            + "        </owl:propertyChainAxiom>\n"
            + "    </owl:ObjectProperty>\n"
            + "    <owl:ObjectProperty rdf:about=\"http://purl.org/net/roles#has_TR\">\n"
            + "        <rdfs:subPropertyOf rdf:resource=\"http://www.w3.org/2002/07/owl#topObjectProperty\"/>\n"
            + "        <owl:propertyChainAxiom rdf:parseType=\"Collection\">\n"
            + "            <rdf:Description rdf:about=\"http://purl.org/net/roles#is_Ac\"/>\n"
            + "            <rdf:Description>\n"
            + "                <owl:inverseOf rdf:resource=\"http://purl.org/net/social-reality#context\"/>\n"
            + "            </rdf:Description>\n"
            + "            <rdf:Description rdf:about=\"http://purl.org/net/roles#is_TR\"/>\n"
            + "            <rdf:Description>\n"
            + "                <owl:inverseOf rdf:resource=\"http://purl.org/net/roles#plays\"/>\n"
            + "            </rdf:Description>\n"
            + "            <rdf:Description rdf:about=\"http://purl.org/net/roles#is_Ag\"/>\n"
            + "        </owl:propertyChainAxiom>\n"
            + "    </owl:ObjectProperty>\n"
            + "    <owl:ObjectProperty rdf:about=\"http://purl.org/net/roles#is_Ac\">\n"
            + "        <rdfs:subPropertyOf rdf:resource=\"http://www.w3.org/2002/07/owl#topObjectProperty\"/>\n"
            + "    </owl:ObjectProperty>\n"
            + "    <owl:ObjectProperty rdf:about=\"http://purl.org/net/roles#is_Ag\">\n"
            + "        <rdfs:subPropertyOf rdf:resource=\"http://www.w3.org/2002/07/owl#topObjectProperty\"/>\n"
            + "    </owl:ObjectProperty>\n"
            + "    <owl:ObjectProperty rdf:about=\"http://purl.org/net/roles#is_Ar\">\n"
            + "        <rdfs:subPropertyOf rdf:resource=\"http://www.w3.org/2002/07/owl#topObjectProperty\"/>\n"
            + "    </owl:ObjectProperty>\n"
            + "    <owl:ObjectProperty rdf:about=\"http://purl.org/net/roles#is_F\">\n"
            + "        <rdfs:subPropertyOf rdf:resource=\"http://www.w3.org/2002/07/owl#topObjectProperty\"/>\n"
            + "    </owl:ObjectProperty>\n"
            + "    <owl:ObjectProperty rdf:about=\"http://purl.org/net/roles#is_R\">\n"
            + "        <rdfs:subPropertyOf rdf:resource=\"http://www.w3.org/2002/07/owl#topObjectProperty\"/>\n"
            + "    </owl:ObjectProperty>\n"
            + "    <owl:ObjectProperty rdf:about=\"http://purl.org/net/roles#is_TR\">\n"
            + "        <rdfs:subPropertyOf rdf:resource=\"http://www.w3.org/2002/07/owl#topObjectProperty\"/>\n"
            + "    </owl:ObjectProperty>\n"
            + "    <owl:ObjectProperty rdf:about=\"http://purl.org/net/roles#plays\">\n"
            + "        <rdfs:subPropertyOf rdf:resource=\"http://purl.org/net/social-reality#counts-as\"/>\n"
            + "    </owl:ObjectProperty>\n"
            + "    <owl:ObjectProperty rdf:about=\"http://purl.org/net/social-reality#context\">\n"
            + "        <rdfs:subPropertyOf rdf:resource=\"http://www.w3.org/2002/07/owl#topObjectProperty\"/>\n"
            + "    </owl:ObjectProperty>\n"
            + "    <owl:ObjectProperty rdf:about=\"http://purl.org/net/social-reality#counts-as\">\n"
            + "        <rdfs:subPropertyOf rdf:resource=\"http://www.w3.org/2002/07/owl#topObjectProperty\"/>\n"
            + "    </owl:ObjectProperty>\n"
            + "    <owl:ObjectProperty rdf:about=\"http://purl.org/net/social-reality#has_OR\">\n"
            + "        <rdfs:subPropertyOf rdf:resource=\"http://www.w3.org/2002/07/owl#topObjectProperty\"/>\n"
            + "        <owl:propertyChainAxiom rdf:parseType=\"Collection\">\n"
            + "            <rdf:Description>\n"
            + "                <owl:inverseOf rdf:resource=\"http://purl.org/net/social-reality#context\"/>\n"
            + "            </rdf:Description>\n"
            + "            <rdf:Description rdf:about=\"http://purl.org/net/social-reality#is_OR\"/>\n"
            + "            <rdf:Description>\n"
            + "                <owl:inverseOf rdf:resource=\"http://purl.org/net/social-reality#counts-as\"/>\n"
            + "            </rdf:Description>\n"
            + "        </owl:propertyChainAxiom>\n"
            + "    </owl:ObjectProperty>\n"
            + "    <owl:ObjectProperty rdf:about=\"http://purl.org/net/social-reality#is_OR\">\n"
            + "        <rdfs:subPropertyOf rdf:resource=\"http://www.w3.org/2002/07/owl#topObjectProperty\"/>\n"
            + "    </owl:ObjectProperty>\n"
            + "    <owl:ObjectProperty rdf:about=\"http://www.w3.org/2002/07/owl#topObjectProperty\"/>\n"
            + "    <owl:Class rdf:about=\"http://purl.org/net/roles#Ac\">\n"
            + "        <owl:equivalentClass>\n"
            + "            <owl:Restriction>\n"
            + "                <owl:onProperty rdf:resource=\"http://purl.org/net/roles#is_Ac\"/>\n"
            + "                <owl:hasSelf rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">true</owl:hasSelf>\n"
            + "            </owl:Restriction>\n"
            + "        </owl:equivalentClass>\n"
            + "        <rdfs:subClassOf rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\"/>\n"
            + "    </owl:Class>\n"
            + "    <owl:Class rdf:about=\"http://purl.org/net/roles#Ag\">\n"
            + "        <owl:equivalentClass>\n"
            + "            <owl:Restriction>\n"
            + "                <owl:onProperty rdf:resource=\"http://purl.org/net/roles#is_Ag\"/>\n"
            + "                <owl:hasSelf rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">true</owl:hasSelf>\n"
            + "            </owl:Restriction>\n"
            + "        </owl:equivalentClass>\n"
            + "        <rdfs:subClassOf rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\"/>\n"
            + "    </owl:Class>\n"
            + "    <owl:Class rdf:about=\"http://purl.org/net/roles#Ar\">\n"
            + "        <owl:equivalentClass>\n"
            + "            <owl:Restriction>\n"
            + "                <owl:onProperty rdf:resource=\"http://purl.org/net/roles#is_Ar\"/>\n"
            + "                <owl:hasSelf rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">true</owl:hasSelf>\n"
            + "            </owl:Restriction>\n"
            + "        </owl:equivalentClass>\n"
            + "    </owl:Class>\n"
            + "    <owl:Class rdf:about=\"http://purl.org/net/roles#F\">\n"
            + "        <owl:equivalentClass>\n"
            + "            <owl:Class>\n"
            + "                <owl:intersectionOf rdf:parseType=\"Collection\">\n"
            + "                    <owl:Restriction>\n"
            + "                        <owl:onProperty rdf:resource=\"http://purl.org/net/social-reality#context\"/>\n"
            + "                        <owl:someValuesFrom rdf:resource=\"http://purl.org/net/roles#Ac\"/>\n"
            + "                    </owl:Restriction>\n"
            + "                    <owl:Restriction>\n"
            + "                        <owl:onProperty>\n"
            + "                            <rdf:Description>\n"
            + "                                <owl:inverseOf rdf:resource=\"http://purl.org/net/roles#plays\"/>\n"
            + "                            </rdf:Description>\n"
            + "                        </owl:onProperty>\n"
            + "                        <owl:someValuesFrom rdf:resource=\"http://purl.org/net/roles#Ar\"/>\n"
            + "                    </owl:Restriction>\n"
            + "                </owl:intersectionOf>\n"
            + "            </owl:Class>\n"
            + "        </owl:equivalentClass>\n"
            + "        <owl:equivalentClass>\n"
            + "            <owl:Restriction>\n"
            + "                <owl:onProperty rdf:resource=\"http://purl.org/net/roles#is_F\"/>\n"
            + "                <owl:hasSelf rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">true</owl:hasSelf>\n"
            + "            </owl:Restriction>\n"
            + "        </owl:equivalentClass>\n"
            + "        <rdfs:subClassOf rdf:resource=\"http://purl.org/net/social-reality#OR\"/>\n"
            + "    </owl:Class>\n"
            + "    <owl:Class rdf:about=\"http://purl.org/net/roles#R\">\n"
            + "        <owl:equivalentClass>\n"
            + "            <owl:Restriction>\n"
            + "                <owl:onProperty>\n"
            + "                    <rdf:Description>\n"
            + "                        <owl:inverseOf rdf:resource=\"http://purl.org/net/roles#plays\"/>\n"
            + "                    </rdf:Description>\n"
            + "                </owl:onProperty>\n"
            + "                <owl:someValuesFrom rdf:resource=\"http://purl.org/net/social-reality#BF\"/>\n"
            + "            </owl:Restriction>\n"
            + "        </owl:equivalentClass>\n"
            + "        <owl:equivalentClass>\n"
            + "            <owl:Restriction>\n"
            + "                <owl:onProperty rdf:resource=\"http://purl.org/net/roles#is_R\"/>\n"
            + "                <owl:hasSelf rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">true</owl:hasSelf>\n"
            + "            </owl:Restriction>\n"
            + "        </owl:equivalentClass>\n"
            + "        <rdfs:subClassOf rdf:resource=\"http://purl.org/net/social-reality#OR\"/>\n"
            + "    </owl:Class>\n"
            + "    <owl:Class rdf:about=\"http://purl.org/net/roles#TR\">\n"
            + "        <owl:equivalentClass>\n"
            + "            <owl:Class>\n"
            + "                <owl:intersectionOf rdf:parseType=\"Collection\">\n"
            + "                    <owl:Restriction>\n"
            + "                        <owl:onProperty rdf:resource=\"http://purl.org/net/social-reality#context\"/>\n"
            + "                        <owl:someValuesFrom rdf:resource=\"http://purl.org/net/roles#Ac\"/>\n"
            + "                    </owl:Restriction>\n"
            + "                    <owl:Restriction>\n"
            + "                        <owl:onProperty>\n"
            + "                            <rdf:Description>\n"
            + "                                <owl:inverseOf rdf:resource=\"http://purl.org/net/roles#plays\"/>\n"
            + "                            </rdf:Description>\n"
            + "                        </owl:onProperty>\n"
            + "                        <owl:someValuesFrom rdf:resource=\"http://purl.org/net/roles#Ag\"/>\n"
            + "                    </owl:Restriction>\n"
            + "                </owl:intersectionOf>\n"
            + "            </owl:Class>\n"
            + "        </owl:equivalentClass>\n"
            + "        <owl:equivalentClass>\n"
            + "            <owl:Restriction>\n"
            + "                <owl:onProperty rdf:resource=\"http://purl.org/net/roles#is_TR\"/>\n"
            + "                <owl:hasSelf rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">true</owl:hasSelf>\n"
            + "            </owl:Restriction>\n"
            + "        </owl:equivalentClass>\n"
            + "        <rdfs:subClassOf rdf:resource=\"http://purl.org/net/social-reality#OR\"/>\n"
            + "    </owl:Class>\n"
            + "    <owl:Class rdf:about=\"http://purl.org/net/social-reality#BF\"/>\n"
            + "    <owl:Class rdf:about=\"http://purl.org/net/social-reality#C\"/>\n"
            + "    <owl:Class rdf:about=\"http://purl.org/net/social-reality#OR\">\n"
            + "        <owl:equivalentClass>\n"
            + "            <owl:Class>\n"
            + "                <owl:intersectionOf rdf:parseType=\"Collection\">\n"
            + "                    <owl:Restriction>\n"
            + "                        <owl:onProperty rdf:resource=\"http://purl.org/net/social-reality#context\"/>\n"
            + "                        <owl:someValuesFrom rdf:resource=\"http://purl.org/net/social-reality#C\"/>\n"
            + "                    </owl:Restriction>\n"
            + "                    <owl:Restriction>\n"
            + "                        <owl:onProperty>\n"
            + "                            <rdf:Description>\n"
            + "                                <owl:inverseOf rdf:resource=\"http://purl.org/net/social-reality#counts-as\"/>\n"
            + "                            </rdf:Description>\n"
            + "                        </owl:onProperty>\n"
            + "                        <owl:someValuesFrom rdf:resource=\"http://purl.org/net/social-reality#BF\"/>\n"
            + "                    </owl:Restriction>\n"
            + "                </owl:intersectionOf>\n"
            + "            </owl:Class>\n"
            + "        </owl:equivalentClass>\n"
            + "        <owl:equivalentClass>\n"
            + "            <owl:Restriction>\n"
            + "                <owl:onProperty rdf:resource=\"http://purl.org/net/social-reality#is_OR\"/>\n"
            + "                <owl:hasSelf rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">true</owl:hasSelf>\n"
            + "            </owl:Restriction>\n"
            + "        </owl:equivalentClass>\n"
            + "        <rdfs:subClassOf>\n"
            + "            <owl:Class>\n"
            + "                <owl:intersectionOf rdf:parseType=\"Collection\">\n"
            + "                    <owl:Restriction>\n"
            + "                        <owl:onProperty rdf:resource=\"http://purl.org/net/social-reality#context\"/>\n"
            + "                        <owl:cardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:cardinality>\n"
            + "                    </owl:Restriction>\n"
            + "                    <owl:Restriction>\n"
            + "                        <owl:onProperty>\n"
            + "                            <rdf:Description>\n"
            + "                                <owl:inverseOf rdf:resource=\"http://purl.org/net/social-reality#counts-as\"/>\n"
            + "                            </rdf:Description>\n"
            + "                        </owl:onProperty>\n"
            + "                        <owl:cardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:cardinality>\n"
            + "                    </owl:Restriction>\n"
            + "                </owl:intersectionOf>\n"
            + "            </owl:Class>\n"
            + "        </rdfs:subClassOf>\n"
            + "    </owl:Class>\n"
            + "    <owl:Class rdf:about=\"http://www.w3.org/2002/07/owl#Thing\"/>\n"
            + "</rdf:RDF>";

    @Test
    public void shouldNotCauseViolationsInput1()
            throws OWLOntologyCreationException {
        OWLOntology o = OWLManager.createOWLOntologyManager()
                .loadOntologyFromOntologyDocument(
                        new ByteArrayInputStream(input1.getBytes()));
        OWL2DLProfile profile = new OWL2DLProfile();
        List<OWLProfileViolation<?>> violations = profile.checkOntology(o)
                .getViolations();
        assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldNotCauseViolationsInput2()
            throws OWLOntologyCreationException {
        OWLOntology o = OWLManager.createOWLOntologyManager()
                .loadOntologyFromOntologyDocument(
                        new ByteArrayInputStream(input2.getBytes()));
        OWL2DLProfile profile = new OWL2DLProfile();
        List<OWLProfileViolation<?>> violations = profile.checkOntology(o)
                .getViolations();
        assertTrue(violations.isEmpty());
    }
}
