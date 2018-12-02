import React, { Component } from 'react';
import './FilterForm.css';
import {getFilterOptions} from '../../../api/API'

class FilterForm extends Component{
    constructor(props) {
        super(props);
        this.state = {

        }
    }

    componentDidMount(){
        getFilterOptions().then((data)=>{
            this.setState(
                {
                    ...this.state,
                    options:data
                })
        })
    }

    capitalizeFirstLetter(string) {
        if(typeof string === 'string'){
            return string.charAt(0).toUpperCase() + string.slice(1);
        }else{
            return string;
        }

    }

    render() {
        let data = this.state.options;
        if(!this.state.options){
            return <div>
                Loading...
            </div>
        }
        let jsx = [];
        for(let category in data){
            if(data.hasOwnProperty(category)){
                let category_options = [];
                for(let i=0;i<data[category].length;i++){
                    category_options.push(<a className="list-group-item col-group-rc">
                        <div className="form-check">
                            <label className="form-check-label">
                                <input className="form-check-input" type="checkbox" name="genre[]"  value={data[category][i]}/>
                                {this.capitalizeFirstLetter(data[category][i])}
                            </label>
                        </div>
                    </a>)
                }
                jsx.push(<div className="col-lg-3">
                    <h4 className="col-heading">{this.capitalizeFirstLetter(category)}</h4>
                    <div className="list-group">
                        {category_options}
                    </div>
                </div>)
            }
        }
        return (

            <div className="row filter-items">
                {jsx}
            </div>
        );
    }

}

export default FilterForm;