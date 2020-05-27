math.randomseed(os.time())
math.random(); math.random(); math.random()

firstNames = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'}
surNames = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'}

request = function()
  rangeFirstNames = table.getn(firstNames) - 1
  rangeSurNames = table.getn(surNames) - 1
  path = "/public/search-users?firstName=" .. firstNames[math.random(rangeFirstNames)] .. "&surName=" .. surNames[math.random(rangeSurNames)]
  -- Return the request object with the current URL path
  return wrk.format('GET', path, {['Host'] = 'localhost'})
end

logfile = io.open("wrk-" .. os.date('%Y-%m-%d_%H_%M_%S') .. ".log", "a+");

response = function(status, header, body)
     logfile:write("status:" .. status .. "\n" .. body .. "\n-------------------------------------------------\n");
end
