package com.example.adoption_Manopata.specification;

import com.example.adoption_Manopata.model.Post;
import org.springframework.data.jpa.domain.Specification;

public class PostSpecification {

    public static Specification<Post> hasProvince(String province) {
        return (root, query, builder) ->
                province != null ? builder.equal(root.get("province"), province) : builder.conjunction();
    }

    public static Specification<Post> hasCity(String city) {
        return (root, query, builder) ->
                city != null ? builder.equal(root.get("city"), city) : builder.conjunction();
    }

    public static Specification<Post> hasBreed(String breed) {
        return (root, query, builder) ->
                breed != null ? builder.equal(root.get("breed"), breed) : builder.conjunction();
    }

    public static Specification<Post> hasAnimalType(String animalType) {
        return (root, query, builder) ->
                animalType != null ? builder.equal(root.get("animalType"), animalType) : builder.conjunction();
    }

    public static Specification<Post> isAvailable(Boolean available) {
        return (root, query, builder) ->
                available != null ? builder.equal(root.get("available"), available) : builder.conjunction();
    }

    public static Specification<Post> isPPP(Boolean isPPP) {
        return (root, query, builder) ->
                isPPP != null ? builder.equal(root.get("ppp"), isPPP) : builder.conjunction();
    }

    public static Specification<Post> isVaccinated(Boolean vaccinated) {
        return (root, query, builder) ->
                vaccinated != null ? builder.equal(root.get("vaccinated"), vaccinated) : builder.conjunction();
    }
}
