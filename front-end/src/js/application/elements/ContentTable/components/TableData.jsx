import React from 'react';
import Streamer from './streamer/Streamer.jsx'
import Summoner from './summoner/Summoner.jsx'

export default class TableData extends React.Component {

    constructor(props) {
        super(props);
        this.removeSummoner = this.removeSummoner.bind(this);
        this.removeStreamer = this.removeStreamer.bind(this);
    }

    static summonersFromArray(array, removeFunc) {
        return array.map(((summoner, index) =>
                <Summoner key={"#" + index + summoner.name + summoner.server} summoner={summoner}
                          removeHandler={removeFunc}/>)
        );
    };

    removeSummoner(streamer, summoner) {
        console.log("Removing summoner: "+ summoner + "in context of streamer: " + streamer);
        this.context.functions.removeSummoner(streamer, summoner);  
    }

    removeStreamer(streamer) {
        console.log("Removing streamer" + streamer);
        //this.context.functions.removeStreamer(streamer);
        let index = this.context.streamers.indexOf(streamer);
        if (index >= 0) {
            this.context.streamers.splice(index, 1);
            this.context.functions.refresh();
        }
    }


    render() {
        return <div className="content">
            {this.context.streamers.map(((value, key)=>
                    <div key={value.name + key.toString()} className="row  entry">
                        <Streamer streamer={value} removeHandler={this.removeStreamer}/>
                        <div className="col summoners-container">
                            {TableData.summonersFromArray(value.summoners, this.removeSummoner.bind(this, value))}
                        </div>
                    </div>)
            )}
        </div>;
    }
}

TableData.contextTypes = {
    streamers: React.PropTypes.array.isRequired,
    functions: React.PropTypes.object.isRequired
};

