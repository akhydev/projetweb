package com.service2.service;

import com.service2.model.Citation;
import com.service2.repository.CitationRepository;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CitationService {
    @Autowired
    private CitationRepository citationRepository;

    public Citation getRandomCitation() {
        return citationRepository.findRandomCitation();
    }

    @PostConstruct
    public void initCitations() {
        if (citationRepository.count() == 0) {
            citationRepository.saveAll(List.of(
                    new Citation("La sécurité est un processus, pas un produit.", "Bruce Schneier"),
                    new Citation(
                            "Si vous pensez que la technologie peut résoudre vos problèmes de sécurité, alors vous ne comprenez ni les problèmes ni la technologie.",
                            "Bruce Schneier"),
                    new Citation(
                            "La sécurité informatique n'est pas une question de technologie, c'est une question de personnes.",
                            "Kevin Mitnick"),
                    new Citation(
                            "La cybersécurité est une course aux armements, et le pirate a toujours une longueur d'avance.",
                            "Stephane Nappo"),
                    new Citation(
                            "Il existe deux types d'entreprises : celles qui ont été piratées, et celles qui ne savent pas encore qu'elles ont été piratées.",
                            "John Chambers"),
                    new Citation("Le maillon le plus faible de la chaîne de sécurité est l'être humain.",
                            "Kevin Mitnick"),
                    new Citation("La sécurité est inversement proportionnelle à la commodité.", "Avi Rubin"),
                    new Citation(
                            "Les mots de passe, c'est comme les sous-vêtements : ne les laissez pas traîner, changez-les régulièrement et ne les partagez avec personne.",
                            "Anonyme"),
                    new Citation("Dans un monde où vous pouvez être n'importe quoi, soyez cybersécurisé.", "Anonyme"),
                    new Citation("La technologie évolue rapidement. La sécurité informatique aussi. Et vous?",
                            "Anonyme")));
        }
    }
}
