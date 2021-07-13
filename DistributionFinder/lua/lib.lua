lib = { }

function lib.stripExtras(line)
    return line
            :gsub("Base.", "")
            :gsub("TEXTURE_", "")
            :gsub("TINT", "")
            :gsub("TEXTURE", "")
            :gsub("farming.", "")
end

return lib