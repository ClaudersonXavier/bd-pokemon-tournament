package com.ufape.projetobanquinhobd.seeder.dtos;

public class MoveDetailResponse {
    private String name;
    private Integer power;
    private DamageClass damage_class;
    private Type type;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Integer getPower() {
        return power;
    }
    public void setPower(Integer power) {
        this.power = power;
    }

    public DamageClass getDamage_class() {
        return damage_class;
    }
    public void setDamage_class(DamageClass damage_class) {
        this.damage_class = damage_class;
    }

    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }

    public static class DamageClass {
        private String name;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class Type {
        private String name;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}