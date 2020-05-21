math.randomseed(os.time())
math.random(); math.random(); math.random()

firstNames = {'Aaron', 'Abel', 'Adolf', 'Adrian', 'Advent', 'Afghan', 'Africa', 'Afro', 'Google'}

request = function()
  path = "/public/search-users?firstName=" .. firstNames[math.random(9)]
  -- Return the request object with the current URL path
  return wrk.format('GET', path, {['Host'] = 'localhost'})
end
