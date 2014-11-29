fin = open('/home/gowayyed/workspace/11791/project-team03/src/main/resources/BioASQ-trainingDataset2b.json')
fout1 = open('/home/gowayyed/workspace/11791/project-team03/src/main/resources/BioASQ-trainingDataset2b-3.json', 'w')
fout = open('/home/gowayyed/workspace/11791/project-team03/src/main/resources/BioASQ-trainingDataset2b-list.json', 'w')
s = "".join(fin.readlines())
import json
pd = json.loads(s)
questionsToWrite = {}
questionsToWrite['questions'] = []
for i in range(len(pd['questions'])):
	pd['questions'][i]['ideal_answer'] = pd['questions'][i]['ideal_answer'][0]
	if pd['questions'][i]['type'] == 'list':
		questionsToWrite['questions'].append({'body': pd['questions'][i]['body'], 'type': pd['questions'][i]['type'], 'id': pd['questions'][i]['id']})
fout1.write(json.dumps(pd, fout1, indent=8))
fout.write(json.dumps(questionsToWrite, fout, indent=8))
fin.close()
fout1.close()
fout.close()
