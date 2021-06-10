for k,v in pairs(distributionTable) do
  print(k)
end

-- while true do

print("\n-----")
print("Please choose one from the list above.")

local choice = io.read()
print("\n")

for k,v in pairs(distributionTable[choice]) do
  print(k)
end

print("\n-----")
print("Please choose one from the list above.")

local choice2 = io.read()
print("\n")

for k,v in pairs(distributionTable[choice][choice2]) do
  print(k,v)
end