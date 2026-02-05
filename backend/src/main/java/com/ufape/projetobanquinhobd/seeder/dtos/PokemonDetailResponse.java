package com.ufape.projetobanquinhobd.seeder.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class PokemonDetailResponse {
    private String name;
    private List<TypeSlot> types;
    private Sprites sprites;
    
    @JsonProperty("past_types")
    private List<PastType> pastTypes;

    public static class TypeSlot {
        private int slot;
        private TypeInfo type;

        public int getSlot() {
            return slot;
        }

        public void setSlot(int slot) {
            this.slot = slot;
        }

        public TypeInfo getType() {
            return type;
        }

        public void setType(TypeInfo type) {
            this.type = type;
        }
    }

    public static class TypeInfo {
        private String name;
        private String url;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class PastType {
        private Generation generation;
        private List<TypeSlot> types;

        public Generation getGeneration() {
            return generation;
        }

        public void setGeneration(Generation generation) {
            this.generation = generation;
        }

        public List<TypeSlot> getTypes() {
            return types;
        }

        public void setTypes(List<TypeSlot> types) {
            this.types = types;
        }
    }

    public static class Generation {
        private String name;
        private String url;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class Sprites {
        @JsonProperty("front_default")
        private String frontDefault;
        
        private Map<String, Object> versions;

        public String getFrontDefault() {
            return frontDefault;
        }

        public void setFrontDefault(String frontDefault) {
            this.frontDefault = frontDefault;
        }

        public Map<String, Object> getVersions() {
            return versions;
        }

        public void setVersions(Map<String, Object> versions) {
            this.versions = versions;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TypeSlot> getTypes() {
        return types;
    }

    public void setTypes(List<TypeSlot> types) {
        this.types = types;
    }

    public Sprites getSprites() {
        return sprites;
    }

    public void setSprites(Sprites sprites) {
        this.sprites = sprites;
    }

    public List<PastType> getPastTypes() {
        return pastTypes;
    }

    public void setPastTypes(List<PastType> pastTypes) {
        this.pastTypes = pastTypes;
    }
}
