import re

def parse_num(s):
    return int(re.sub(r'[,_]', '', s))

with open('d:/ihanuat/pet_levels_raw.txt', 'r') as f:
    lines = f.readlines()

data = {}
for rarity in ['Common', 'Uncommon', 'Rare', 'Epic', 'Legendary']:
    data[rarity] = [0] * 101 # Index 0 unused, Index 1-100 levels

current_level = 0
for line in lines:
    parts = line.split()
    if not parts or not parts[0].isdigit():
        continue
    
    level = int(parts[0])
    # Expecting: Level, P_Common, C_Common, P_Uncommon, C_Uncommon, P_Rare, C_Rare, P_Epic, C_Epic, P_Leg, C_Leg
    # Total 11 parts
    if len(parts) < 11:
        continue
        
    data['Common'][level] = parse_num(parts[2])
    data['Uncommon'][level] = parse_num(parts[4])
    data['Rare'][level] = parse_num(parts[6])
    data['Epic'][level] = parse_num(parts[8])
    data['Legendary'][level] = parse_num(parts[10])

with open('d:/ihanuat/arrays_out.txt', 'w', encoding='utf-8') as out:
    def print_java_array(name, values):
        out.write(f"    private static final long[] {name} = {{\n")
        out.write("            0L,\n") # index 0
        for i in range(1, 101, 10):
            chunk = values[i : i+10]
            line = ", ".join(f"{v}L" for v in chunk)
            out.write(f"            {line},\n")
        out.write("    };\n\n")

    for rarity in ['Common', 'Uncommon', 'Rare', 'Epic', 'Legendary']:
        print_java_array(f"XP_1_100_{rarity.upper()}", data[rarity])
