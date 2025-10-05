ResearchdEvents.registerResearchPacks(event => {
    event.create('factorio:automation_science')
        .colorRGB(255, 51, 51)
        .sortingValue(1)
    
    event.create('factorio:logistic_science')
        .colorRGB(34, 221, 68)
        .sortingValue(2)
    
    event.create('factorio:military_science')
        .colorRGB(102, 102, 102)
        .sortingValue(3)
    
    event.create('factorio:chemical_science')
        .colorRGB(51, 153, 255)
        .sortingValue(4)
    
    event.create('factorio:production_science')
        .colorRGB(170, 51, 255)
        .sortingValue(5)
    
    event.create('factorio:utility_science')
        .colorRGB(255, 255, 51)
        .sortingValue(6)
})

ResearchdEvents.registerResearches(event => {
    event.create('factorio:start')
        .icon('minecraft:book')
        .consumeItem('minecraft:dirt', 1)
        .effect(ResearchEffectHelper.empty())
        .literalName('Factory Start')
        .literalDescription('Begin your industrial journey')
        .noParentRequired()
    
    event.create('factorio:basic_automation')
        .icon('minecraft:iron_ingot')
        .parent('factorio:start')
        .consumePack('factorio:automation_science', 10, 5)
        .effect(ResearchEffectHelper.unlockRecipe('minecraft:hopper'))
        .literalName('Basic Automation')
        .literalDescription('Learn the fundamentals of automated production')
    
    event.create('factorio:stone_furnace')
        .icon('minecraft:furnace')
        .parent('factorio:start')
        .consumePack('factorio:automation_science', 5, 3)
        .effect(ResearchEffectHelper.unlockRecipe('minecraft:furnace'))
        .literalName('Stone Furnace')
        .literalDescription('Unlock basic smelting capabilities')
    
    event.create('factorio:automation_2')
        .icon('minecraft:piston')
        .parent('factorio:basic_automation')
        .consumePack('factorio:automation_science', 20, 10)
        .effect(ResearchEffectHelper.unlockRecipe('minecraft:piston'))
        .literalName('Automation II')
        .literalDescription('Advanced automation techniques')
    
    event.create('factorio:logistics')
        .icon('minecraft:chest_minecart')
        .parent('factorio:basic_automation')
        .method(ResearchMethodHelper.and(
            ResearchMethodHelper.consumePack('factorio:automation_science', 15, 10),
            ResearchMethodHelper.consumePack('factorio:logistic_science', 15, 10)
        ))
        .effect(ResearchEffectHelper.unlockRecipe('minecraft:chest_minecart'))
        .literalName('Logistics')
        .literalDescription('Efficient item transportation systems')
    
    event.create('factorio:steel_processing')
        .icon('minecraft:iron_block')
        .parent('factorio:stone_furnace')
        .method(ResearchMethodHelper.and(
            ResearchMethodHelper.consumePack('factorio:automation_science', 25, 15),
            ResearchMethodHelper.consumePack('factorio:logistic_science', 25, 15)
        ))
        .effect(ResearchEffectHelper.unlockRecipe('minecraft:iron_block'))
        .literalName('Steel Processing')
        .literalDescription('Advanced metalworking techniques')
    
    event.create('factorio:advanced_electronics')
        .icon('minecraft:comparator')
        .parent('factorio:automation_2')
        .method(ResearchMethodHelper.and(
            ResearchMethodHelper.consumePack('factorio:automation_science', 30, 20),
            ResearchMethodHelper.consumePack('factorio:logistic_science', 30, 20)
        ))
        .effect(ResearchEffectHelper.unlockRecipe('minecraft:comparator'))
        .literalName('Advanced Electronics')
        .literalDescription('Complex circuitry and logic systems')
    
    event.create('factorio:military')
        .icon('minecraft:tnt')
        .parent('factorio:steel_processing')
        .method(ResearchMethodHelper.and(
            ResearchMethodHelper.consumePack('factorio:automation_science', 20, 15),
            ResearchMethodHelper.consumePack('factorio:logistic_science', 20, 15),
            ResearchMethodHelper.consumePack('factorio:military_science', 20, 15)
        ))
        .effect(ResearchEffectHelper.unlockRecipe('minecraft:tnt'))
        .literalName('Military')
        .literalDescription('Defensive and offensive capabilities')
    
    event.create('factorio:oil_processing')
        .icon('minecraft:coal')
        .parent('factorio:steel_processing')
        .method(ResearchMethodHelper.and(
            ResearchMethodHelper.consumePack('factorio:automation_science', 40, 25),
            ResearchMethodHelper.consumePack('factorio:logistic_science', 40, 25)
        ))
        .effect(ResearchEffectHelper.and(
            ResearchEffectHelper.unlockRecipe('minecraft:coal_block'),
            ResearchEffectHelper.unlockNether()
        ))
        .literalName('Oil Processing')
        .literalDescription('Unlock petroleum processing')
    
    event.create('factorio:advanced_oil_processing')
        .icon('minecraft:blaze_powder')
        .parent('factorio:oil_processing')
        .method(ResearchMethodHelper.and(
            ResearchMethodHelper.consumePack('factorio:automation_science', 50, 30),
            ResearchMethodHelper.consumePack('factorio:logistic_science', 50, 30),
            ResearchMethodHelper.consumePack('factorio:chemical_science', 50, 30)
        ))
        .effect(ResearchEffectHelper.unlockRecipe('minecraft:blaze_powder'))
        .literalName('Advanced Oil Processing')
        .literalDescription('Optimize petroleum refinement')
    
    event.create('factorio:battery')
        .icon('minecraft:redstone_block')
        .parent('factorio:oil_processing')
        .method(ResearchMethodHelper.and(
            ResearchMethodHelper.consumePack('factorio:automation_science', 30, 20),
            ResearchMethodHelper.consumePack('factorio:logistic_science', 30, 20),
            ResearchMethodHelper.consumePack('factorio:chemical_science', 30, 20)
        ))
        .effect(ResearchEffectHelper.unlockRecipe('minecraft:redstone_block'))
        .literalName('Battery')
        .literalDescription('Energy storage technology')
    
    event.create('factorio:robotics')
        .icon('minecraft:observer')
        .parents('factorio:advanced_electronics', 'factorio:battery')
        .method(ResearchMethodHelper.and(
            ResearchMethodHelper.consumePack('factorio:automation_science', 75, 40),
            ResearchMethodHelper.consumePack('factorio:logistic_science', 75, 40),
            ResearchMethodHelper.consumePack('factorio:chemical_science', 75, 40)
        ))
        .effect(ResearchEffectHelper.unlockRecipe('minecraft:observer'))
        .literalName('Robotics')
        .literalDescription('Autonomous construction and logistics')
    
    event.create('factorio:electric_furnace')
        .icon('minecraft:blast_furnace')
        .parents('factorio:steel_processing', 'factorio:advanced_electronics')
        .method(ResearchMethodHelper.and(
            ResearchMethodHelper.consumePack('factorio:automation_science', 60, 35),
            ResearchMethodHelper.consumePack('factorio:logistic_science', 60, 35),
            ResearchMethodHelper.consumePack('factorio:chemical_science', 60, 35)
        ))
        .effect(ResearchEffectHelper.unlockRecipe('minecraft:blast_furnace'))
        .literalName('Electric Furnace')
        .literalDescription('High-efficiency electrical smelting')
    
    event.create('factorio:productivity_module')
        .icon('minecraft:diamond')
        .parent('factorio:advanced_electronics')
        .method(ResearchMethodHelper.and(
            ResearchMethodHelper.consumePack('factorio:automation_science', 100, 50),
            ResearchMethodHelper.consumePack('factorio:logistic_science', 100, 50),
            ResearchMethodHelper.consumePack('factorio:chemical_science', 100, 50),
            ResearchMethodHelper.consumePack('factorio:production_science', 100, 50)
        ))
        .effect(ResearchEffectHelper.unlockRecipe('minecraft:diamond'))
        .literalName('Productivity Module')
        .literalDescription('Increase production efficiency')
    
    event.create('factorio:rocket_fuel')
        .icon('minecraft:fire_charge')
        .parent('factorio:advanced_oil_processing')
        .method(ResearchMethodHelper.and(
            ResearchMethodHelper.consumePack('factorio:automation_science', 80, 45),
            ResearchMethodHelper.consumePack('factorio:logistic_science', 80, 45),
            ResearchMethodHelper.consumePack('factorio:chemical_science', 80, 45),
            ResearchMethodHelper.consumePack('factorio:production_science', 80, 45)
        ))
        .effect(ResearchEffectHelper.unlockRecipe('minecraft:fire_charge'))
        .literalName('Rocket Fuel')
        .literalDescription('High-energy propellant')
    
    event.create('factorio:nuclear_power')
        .icon('minecraft:emerald_block')
        .parents('factorio:electric_furnace', 'factorio:rocket_fuel')
        .method(ResearchMethodHelper.and(
            ResearchMethodHelper.consumePack('factorio:automation_science', 150, 60),
            ResearchMethodHelper.consumePack('factorio:logistic_science', 150, 60),
            ResearchMethodHelper.consumePack('factorio:chemical_science', 150, 60),
            ResearchMethodHelper.consumePack('factorio:production_science', 150, 60)
        ))
        .effect(ResearchEffectHelper.unlockRecipe('minecraft:emerald_block'))
        .literalName('Nuclear Power')
        .literalDescription('Harness the power of the atom')
    
    event.create('factorio:utility_science_pack')
        .icon('minecraft:dragon_breath')
        .parents('factorio:robotics', 'factorio:nuclear_power')
        .method(ResearchMethodHelper.and(
            ResearchMethodHelper.consumePack('factorio:automation_science', 200, 80),
            ResearchMethodHelper.consumePack('factorio:logistic_science', 200, 80),
            ResearchMethodHelper.consumePack('factorio:chemical_science', 200, 80),
            ResearchMethodHelper.consumePack('factorio:production_science', 200, 80)
        ))
        .effect(ResearchEffectHelper.and(
            ResearchEffectHelper.unlockRecipe('minecraft:dragon_breath'),
            ResearchEffectHelper.unlockEnd()
        ))
        .literalName('Utility Science Pack')
        .literalDescription('Unlock advanced research capabilities')
    
    event.create('factorio:rocket_silo')
        .icon('minecraft:end_crystal')
        .parent('factorio:utility_science_pack')
        .method(ResearchMethodHelper.and(
            ResearchMethodHelper.consumePack('factorio:automation_science', 500, 100),
            ResearchMethodHelper.consumePack('factorio:logistic_science', 500, 100),
            ResearchMethodHelper.consumePack('factorio:chemical_science', 500, 100),
            ResearchMethodHelper.consumePack('factorio:production_science', 500, 100),
            ResearchMethodHelper.consumePack('factorio:utility_science', 500, 100)
        ))
        .effect(ResearchEffectHelper.unlockRecipe('minecraft:end_crystal'))
        .literalName('Rocket Silo')
        .literalDescription('Construct a launch facility')
    
    event.create('factorio:space_science')
        .icon('minecraft:nether_star')
        .parent('factorio:rocket_silo')
        .method(ResearchMethodHelper.and(
            ResearchMethodHelper.consumePack('factorio:automation_science', 1000, 150),
            ResearchMethodHelper.consumePack('factorio:logistic_science', 1000, 150),
            ResearchMethodHelper.consumePack('factorio:chemical_science', 1000, 150),
            ResearchMethodHelper.consumePack('factorio:production_science', 1000, 150),
            ResearchMethodHelper.consumePack('factorio:utility_science', 1000, 150)
        ))
        .effect(ResearchEffectHelper.unlockRecipe('minecraft:nether_star'))
        .literalName('Space Science')
        .literalDescription('Research from beyond the atmosphere')
    
    event.create('factorio:laser_turret')
        .icon('minecraft:beacon')
        .parents('factorio:military', 'factorio:battery')
        .method(ResearchMethodHelper.and(
            ResearchMethodHelper.consumePack('factorio:automation_science', 100, 45),
            ResearchMethodHelper.consumePack('factorio:logistic_science', 100, 45),
            ResearchMethodHelper.consumePack('factorio:military_science', 100, 45),
            ResearchMethodHelper.consumePack('factorio:chemical_science', 100, 45)
        ))
        .effect(ResearchEffectHelper.unlockRecipe('minecraft:beacon'))
        .literalName('Laser Turret')
        .literalDescription('Advanced defensive structures')
    
    event.create('factorio:power_armor')
        .icon('minecraft:netherite_chestplate')
        .parents('factorio:military', 'factorio:advanced_electronics')
        .method(ResearchMethodHelper.and(
            ResearchMethodHelper.consumePack('factorio:automation_science', 150, 60),
            ResearchMethodHelper.consumePack('factorio:logistic_science', 150, 60),
            ResearchMethodHelper.consumePack('factorio:military_science', 150, 60),
            ResearchMethodHelper.consumePack('factorio:chemical_science', 150, 60)
        ))
        .effect(ResearchEffectHelper.unlockRecipe('minecraft:netherite_chestplate'))
        .literalName('Power Armor')
        .literalDescription('Personal combat equipment')
    
    event.create('factorio:construction_robotics')
        .icon('minecraft:dispenser')
        .parent('factorio:robotics')
        .method(ResearchMethodHelper.and(
            ResearchMethodHelper.consumePack('factorio:automation_science', 120, 50),
            ResearchMethodHelper.consumePack('factorio:logistic_science', 120, 50),
            ResearchMethodHelper.consumePack('factorio:chemical_science', 120, 50),
            ResearchMethodHelper.consumePack('factorio:production_science', 120, 50)
        ))
        .effect(ResearchEffectHelper.unlockRecipe('minecraft:dispenser'))
        .literalName('Construction Robotics')
        .literalDescription('Automated building capabilities')
    
    event.create('factorio:logistic_robotics')
        .icon('minecraft:dropper')
        .parent('factorio:robotics')
        .method(ResearchMethodHelper.and(
            ResearchMethodHelper.consumePack('factorio:automation_science', 120, 50),
            ResearchMethodHelper.consumePack('factorio:logistic_science', 120, 50),
            ResearchMethodHelper.consumePack('factorio:chemical_science', 120, 50),
            ResearchMethodHelper.consumePack('factorio:production_science', 120, 50)
        ))
        .effect(ResearchEffectHelper.unlockRecipe('minecraft:dropper'))
        .literalName('Logistic Robotics')
        .literalDescription('Automated item delivery systems')
})
